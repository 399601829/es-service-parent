package com.es.service.index.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.type.IndexType;
import com.es.service.common.util.JsonUtil;

/**
 * 
 * 编入索引控制器
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月18日
 * 
 */
public class IndexDispatcher {

    /**
     * 
     */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * 事务容器
     */
    private static Map<String, IndexTransaction> transactions = new HashMap<String, IndexTransaction>();

    /**
     * 事务id
     * 
     */
    private String transactionId;

    /**
     * 
     * @param indexType
     * @param needFull
     */
    private IndexDispatcher(IndexType indexType, boolean needFull) {
        LOGGER.debug("createDispatcher 开始！"+needFull+"-"+Thread.currentThread().getId()+"-"+Thread.currentThread().getName());
        IndexTransaction transaction = IndexTransaction.createTransaction(indexType, needFull);
        if (transaction == null) {
            throw new RuntimeException(String.format(
                    "createIndexTransaction fail ! indexname:%s,needFull:%s",
                    indexType.index_type(), needFull));
        }
        transactions.put(transaction.getTransactionId(), transaction);
        this.transactionId = transaction.getTransactionId();
    }

    /**
     * 创建事务控制器
     * 
     * @param indexType
     * @param needFull
     * @return
     */
    public static IndexDispatcher createDispatcher(IndexType indexType, boolean needFull) {
        IndexDispatcher indexDispatcher = new IndexDispatcher(indexType, needFull);
        putAndClear();
        return indexDispatcher;
    }

    /**
     * 保存and清理
     */
    private static void putAndClear() {
        if (transactions.size() > 30) {
            Set<Entry<String, IndexTransaction>> entrySet = transactions.entrySet();
            long currentTime = System.currentTimeMillis();
            for (Entry<String, IndexTransaction> e : entrySet) {
                String createTime = e.getKey().split("_")[1];
                // 超时删除
                if (108000000 < (currentTime - Long.valueOf(createTime))) {
                    transactions.remove(e.getKey());
                }
            }
        }
    }

    /**
     * 获取当前事务
     * 
     * @return
     */
    private IndexTransaction getCurrentTransaction() {
        return transactions.get(transactionId);
    }

    /**
     * 新增索引
     * 
     * @param jsonObject
     * @return
     */
    public int indexed(String jsonObject) {
        return getCurrentTransaction().indexed(jsonObject);
    }

    /**
     * 新增索引
     * @param <T>
     * 
     * @param tos
     * @return
     */
    public <T> void indexedList(List<?> tos) {
        IndexTransaction currentTransaction = getCurrentTransaction();
        for (Object o : tos) {
            if (o instanceof String) {
                currentTransaction.indexed(o.toString());
            } else {
                currentTransaction.indexed(JsonUtil.toJson(o));
            }
        }
    }

    /**
     * 删除文档数据
     * 
     * @param docId
     * @return
     */
    public boolean delDoc(String docId) {
        return getCurrentTransaction().deleteDocById(docId);
    }

    /**
     * 删除整个索引
     * 
     * @param index_type 索引名称
     * @return
     */
    public boolean delIndex(String index_type) {
        return getCurrentTransaction().deleteIndex(index_type);
    }

    /**
     * 提交索引
     * 
     * @return CurrentIndexName
     */
    public String commit() {
        IndexTransaction currentTransaction = getCurrentTransaction();
        if (currentTransaction == null) {
            LOGGER.error("The transaction failed ! Because the timeout.");
            return null;
        }
        currentTransaction.commitIndex();
        return currentTransaction.getCurrentIndexName();
    }

}
