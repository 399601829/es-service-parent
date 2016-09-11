package com.es.service.search.type;

/**
 * 搜索类型
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月30日
 *
 */
public enum SearchType {

    /**
     * 分词查询
     */
    QUERY_STRING,

    /**
     * 短语匹配
     */
    MATCHING_PHRASE,

    /**
     * 边界值查询
     */
    RANGE,

    /**
     * 精确匹配
     */
    TERM,

    /**
     * 模糊匹配（容错匹配）
     */
    FUZZ,

    /**
     * 前缀匹配
     */
    PREFIX;
}
