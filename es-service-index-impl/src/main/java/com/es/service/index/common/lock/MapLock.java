package com.es.service.index.common.lock;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * 
 * 单机锁-本地测试用
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月18日
 * 
 */
public class MapLock extends DistributedLock {

    private static final ConcurrentHashMap<String, String> locks = new ConcurrentHashMap<String, String>();
    /**
     * lock的key
     */
    private String key;

    /**
     * lock的心跳时间(毫秒)
     * <p>
     * 必须满足(heartbeatTime <= timeout*1000)
     */
    private long heartbeatTime;

    /**
     * lock的自然超时时间(秒)
     */
    private int timeout;

    /**
     * 版本号时间，作为获取锁的客户端操作的依据
     */
    private long versionTime;

    /**
     * 是否快速失败
     */
    private boolean fastfail;

    /**
     * 
     * @param key
     * @param timeout
     */
    public MapLock(String key, int timeout) {
        this(key, timeout * 1000, timeout, true);
    }

    /**
     * 
     * @param key
     * @param heartbeatTime 必须满足(heartbeatTime <= timeout*1000)
     * @param timeout
     */
    public MapLock(String key, long heartbeatTime, int timeout) {
        this(key, heartbeatTime, timeout, (heartbeatTime == timeout * 1000 ? true : false));
    }

    /**
     * 
     * @param key
     * @param heartbeatTime 必须满足(heartbeatTime <= timeout*1000)
     * @param timeout
     * @param fastfail 快速失败只在heartbeatTime == timeout * 1000时才有意义
     */
    public MapLock(String key, long heartbeatTime, int timeout, boolean fastfail) {
        Preconditions.checkArgument(heartbeatTime <= timeout * 1000,
                "info:heartbeatTime 必须满足(heartbeatTime <= timeout*1000) ");
        this.key = key;
        this.heartbeatTime = heartbeatTime;
        this.timeout = timeout;
        this.fastfail = fastfail;
    }

    /**
     * 获取锁
     * 
     */
    protected boolean lock() {
        if (locks.putIfAbsent(key, buildVal()) == null) {
            return true;
        }
        if (System.currentTimeMillis() < getLong(locks.get(key))) {
            locks.remove(key);
            return lock();
        }
        return false;

    }

    /**
     * 检查所是否有效
     * 
     * @return
     */
    public boolean check() {
        long getVal = getLong(locks.get(key));
        return System.currentTimeMillis() < getVal && versionTime == getVal;
    }

    /**
     * 维持心跳，仅在heartbeatTime < timeout时需要
     * <p>
     * 如果heartbeatTime == timeout，此操作是没有意义的
     * 
     * @return
     */
    @Override
    public boolean heartbeat() {
        // 1. 避免操作非自己获取得到的锁
        return check() && getLong(locks.put(key, buildVal())) != 0;
    }

    /**
     * 释放锁
     */
    public boolean unLock() {
        // 1. 避免删除非自己获取得到的锁
        return check() && locks.remove(key) != null;
    }

    /**
     * if value==null || value=="" <br>
     * &nbsp return 0 <br>
     * else <br>
     * &nbsp return Long.valueOf(value)
     * 
     * @param value
     * @return
     */
    private long getLong(String value) {
        return StringUtils.isBlank(value) ? 0 : Long.valueOf(value);
    }

    /**
     * 生成val,当前系统时间+心跳时间
     * 
     * @return System.currentTimeMillis() + heartbeatTime + 1
     */
    private String buildVal() {
        versionTime = System.currentTimeMillis() + heartbeatTime + 1;
        return String.valueOf(versionTime);
    }
}
