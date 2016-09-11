package com.es.service.index.common.lock;

import com.es.service.common.type.IndexType;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月23日
 * 
 */
public class IndexLockHelper {

    private static volatile ThreadLocal<DistributedLock> local = new InheritableThreadLocal();

    /**
     * 获取锁
     * 
     * @param lock
     * @return
     */
    public static boolean lock(IndexUpdateLock lock) {
        String currentKey = "index_%s";
        currentKey = lock.isFull() ? String.format(currentKey, "full_%s") : currentKey;
        currentKey = lock.isDelete() ? String.format(currentKey, "delete_%s") : currentKey;
        currentKey = String.format(currentKey, lock.indexType().getIndexNo());

        DistributedLock currentLock = getLock(currentKey);
        // 锁失败
        if (!currentLock.tryLock()) {
            return false;
        }
        // 获取到全量索引锁
        if (lock.isFull()) {
            local.set(currentLock);
            return true;
        }

        // 增量索引要检测是否正在进行全量索引任务
        String fullKey = String.format("index_full_%s", lock.indexType().getIndexNo());
        DistributedLock fullLock = getLock(fullKey);
        // 全量索引正在创建，不能进行增量索引创建任务
        if (fullLock.check()) {
            return false;
        }

        // 全量索引没有创建说明可以进行增量索引创建任务
        local.set(currentLock);
        return true;
    }

    /**
     * 心跳锁
     * 
     * @param indexType
     * @param isfull
     */
    public static void heartbeat(IndexType indexType, boolean isfull) {
        DistributedLock lock = local.get();
        if (lock != null) {
            lock.heartbeat();
        }
        /*
         * String currentKey = "index_%s"; currentKey = isfull ? String.format(currentKey, "full_%s") : currentKey; currentKey =
         * String.format(currentKey, indexType.getIndexNo()); getLock(currentKey).heartbeat();
         */
    }

    /**
     * 释放锁
     * 
     * @param indexType void
     */
    public static void unLock(IndexUpdateLock lock) {
        /*
         * String currentKey = "index_%s"; currentKey = lock.isFull() ? String.format(currentKey, "full_%s") : currentKey; currentKey =
         * lock.isDelete() ? String.format(currentKey, "delete_%s") : currentKey; currentKey = String.format(currentKey,
         * lock.indexType().getIndexNo());
         */

        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // getLock(currentKey).unLock();
        DistributedLock redisLock = local.get();
        if (redisLock != null) {
            local.remove();
            redisLock.unLock();

        }

    }

    /**
     * 获取锁
     * 
     * @param currentKey
     * @return
     */
    private static DistributedLock getLock(String currentKey) {
        // return new RedisDistributedLock(currentKey, 30, 1 * 24 * 60);
        return new MapLock(currentKey, 30, 1 * 24 * 60);
    }

}
