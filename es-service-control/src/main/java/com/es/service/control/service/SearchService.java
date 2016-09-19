package com.es.service.control.service;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月6日
 * 
 */
public interface SearchService {

    /**
     * 搜索
     * 
     * @param indexName
     * @param typeName
     * @param keyWord
     * @return
     */
    String search(String indexName, String typeName, String keyWord);

    /**
     * 建议器
     * 
     * @param indexName
     * @param keyWord
     * @return
     */
    String suggest(String indexName, String keyWord);
}
