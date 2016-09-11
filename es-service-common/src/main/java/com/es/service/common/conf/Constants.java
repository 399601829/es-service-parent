package com.es.service.common.conf;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月28日
 * 
 */
public final class Constants {

    /**
     * es索引信息存放目录
     */
    public static final String es_index_info_dir = Conf.getString("es_index_info_dir");

    /**
     * es索引分片数量
     */
    public static final int index_number_of_shards = Conf.getInt("index.number_of_shards");

    /**
     * es索引副本数量
     */
    public static final int index_number_of_replicas = Conf.getInt("index.number_of_replicas");

    /**
     * 是否为debug模式
     */
    public static final boolean isDebug = Conf.getBoolean("isDebug");

    /**
     * 搜索偏好
     */
    public static final String preference = Conf.getString("search.preference");

    /**
     * 脚本得分与相似度得分聚合模式
     */
    public static final String boost_mode = Conf.getString("search.boost_mode");

    /**
     * 脚本得分与脚本得分聚合模式
     */
    public static final String score_mode = Conf.getString("search.score_mode");

    /****************************** 扩展常用key ***********************************/

    /**
     * 脚本得分与相似度得分聚合模式
     */
    public static final String BOOST_MODE_KEY = "BOOST_MODE";

}
