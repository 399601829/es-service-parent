package com.es.service.common.util;

import java.util.List;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.indices.IndexMissingException;

import com.es.service.common.client.ESClient;
import com.es.service.common.type.IndexType;

/**
 * 
 * 分词工具
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月19日
 * 
 */
public class AnalyzeHelper {

    /**
     * 默认分词器
     */
    private static final String analyzer = "ik";

    /**
     * 默认分词的索引
     */
    private static volatile String indexName;

    private static volatile boolean reLoad;

    /**
     * ik分词-无法分词则返回空集合
     * 
     * @param str
     * @return
     */
    public static List<String> analyze(String str) {
        return analyze(analyzer, str);
    }

    /**
     * 分词-无法分词则返回空集合
     * 
     * @param analyzer
     * @param str
     * @return
     */
    public static List<String> analyze(String analyzer, String str) {

        AnalyzeResponse ar = null;
        try {
            AnalyzeRequest request = new AnalyzeRequest(str).analyzer(analyzer).index(
                    getCurrentValidIndex());
            ar = ESClient.getClient().admin().indices().analyze(request).actionGet();
        } catch (IndexMissingException e) {
            if (!reLoad) {
                synchronized (AnalyzeHelper.class) {
                    if (!reLoad) {
                        reLoad = true;
                    }
                }
            }
            return analyze(analyzer, str);
        }

        if (ar == null || ar.getTokens() == null || ar.getTokens().size() < 1) {
            return Lists.newArrayList();
        }
        List<String> analyzeTokens = Lists.newArrayList();
        for (AnalyzeToken at : ar.getTokens()) {
            analyzeTokens.add(at.getTerm());
        }
        return analyzeTokens;
    }

    /**
     * 获取当前有效的索引名
     * 
     * @return
     */
    public static String getCurrentValidIndex() {
        IndexType indexType = IndexType.RESOURCES;
        if (indexName != null) {
            if (reLoad) {
                synchronized (AnalyzeHelper.class) {
                    if (reLoad) {
                        reLoad = false;
                        indexName = indexName.equals(indexType.index_type_1()) ? indexType
                                .index_type_2() : indexType.index_type_1();
                    }
                }
            }
            return indexName;
        }
        IndicesAdminClient adminClient = ESClient.getClient().admin().indices();
        if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()) {
            indexName = indexType.index_type_1();
        } else if (adminClient.prepareExists(indexType.index_type_2()).execute().actionGet()
                .isExists()) {
            indexName = indexType.index_type_2();
        }
        return indexName;
    }

}
