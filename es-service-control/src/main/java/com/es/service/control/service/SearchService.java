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
     * 
     * @param indexName
     * @param typeName
     * @param keyWord
     * @return
     */
    String search(String indexName, String typeName, String keyWord);
}
