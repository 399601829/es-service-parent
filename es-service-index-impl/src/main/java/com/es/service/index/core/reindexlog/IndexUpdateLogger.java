package com.es.service.index.core.reindexlog;

import com.es.service.common.type.IndexType;

/**
 * 
 * 索引更新日志
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月19日
 * 
 */
public interface IndexUpdateLogger {

    /**
     * 获取更新时间
     * 
     * @param indexType
     * @param specifiedTime
     * @return
     * @throws Exception
     */
    public Long getLastIndexTime(IndexType indexType, long specifiedTime);

    /**
     * 获取更新索引名
     * 
     * @param indexType
     * @return
     */
    public String getLastIndexName(IndexType indexType);

    /**
     * 写入更新记录
     * 
     * @param indexType
     * @param indexName
     * @param indexTime
     */
    public void log(IndexType indexType, String indexName, long indexTime);

}
