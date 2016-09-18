package com.es.service.search.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion.Entry.Option;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionContext;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionFuzzyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.client.ESClient;
import com.es.service.common.conf.Constants;
import com.es.service.common.util.CompletionSuggest.SuggestQuery;
import com.es.service.common.util.JsonUtil;
import com.es.service.common.util.PinYinHelper;
import com.es.service.search.to.EsRequest;
import com.es.service.search.to.EsResponse;
import com.google.common.collect.Lists;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月31日
 * 
 */
public class EsSearch {

    /**
     * 
     */
    private static Logger logger = LoggerFactory.getLogger(EsSearch.class);

    /**
     * 搜索入口
     * 
     * @param request
     * @param timeout
     * @param timeUnit
     * @return
     */
    public static EsResponse search(EsRequest request, int timeout, TimeUnit timeUnit) {
        // 索引
        String indexname = request.getIndexname();
        // 结束位置
        int size = request.getPs();
        // 开始位置
        int from = request.getPn() < 0 ? 0 : (request.getPn() - 1) * size;

        // 主查询体
        SearchRequestBuilder srb = ESClient.getClient().prepareSearch(indexname)
                .setTypes(request.getTypes().toArray(new String[] {})).setFrom(from).setSize(size)
                .setPreference(Constants.preference).setTimeout(new TimeValue(timeout, timeUnit));

        // 构造查询体
        EsQueryBuilder esQueryBuilder = new EsQueryBuilder(request);
        esQueryBuilder.makeFilterBuilder(srb);
        if (esQueryBuilder.makeQueryBuilder(srb) == null) {
            return new EsResponse();
        }

        // 处理返回字段
        if (!request.getSafeResultFileds().isEmpty()) {
            srb.addFields(request.getSafeResultFileds().toArray(new String[] {}));
        }
        // 高亮
        EsHighLight.setHighLight(srb, request.getSafeHighlightFields());
        // 排序
        EsSort.sortAdapter(srb, request.getSafeSortFileds());
        // 调试模式
        if (Constants.isDebug) {
            srb.setExplain(true);
            logger.info("\r\n" + srb.toString());
        }

        // 返回结果
        SearchResponse response = srb.execute().actionGet();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> record = hit.getSource();
            if (record == null && (record = new HashMap<String, Object>()) != null) {
                for (Map.Entry<String, SearchHitField> e : hit.fields().entrySet()) {
                    record.put(e.getKey(), e.getValue().getValue());
                }
            }
            record.put("ESSCORE", hit.getScore());
            // 高亮返回
            if (request.getSafeHighlightFields() != null) {
                record.putAll(EsHighLight.getHighlight(hit, request.getSafeHighlightFields()
                        .getFields()));
            }
            result.add(record);
        }

        // 调试模式
        if (Constants.isDebug) {
            logger.info("search result :{}", JsonUtil.toJson(result));
            logger.info("search TotalHits :{}", response.getHits().getTotalHits());
            logger.info("search TookInMillis :{}", response.getTookInMillis());
        }

        logger.info("search log for index:{},keys:{}", indexname, request.getAllValue());
        return new EsResponse((int) response.getHits().getTotalHits(), JsonUtil.toJson(result), 0);
    }

    /**
     * 建议器搜索
     * 
     * @param request
     * @return
     */
    public static EsResponse suggestSearch(EsRequest request) {

        List<Map<String, Object>> suggest = getCompletionSuggest(request.getIndexname(),
                request.getSafeSuggestQuery());
        return new EsResponse(request.getSafeSuggestQuery().getSize(), JsonUtil.toJson(suggest), 0);
    }

    /**
     * 
     * @param indices
     * @param suggestQuery
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    private static List<Map<String, Object>> getCompletionSuggest(String indices,
            SuggestQuery suggestQuery) {
        // 查询体
        CompletionSuggestionFuzzyBuilder suggestionsBuilder = new CompletionSuggestionFuzzyBuilder(
                suggestQuery.getSuggestname());
        suggestionsBuilder.setFuzziness(Fuzziness.AUTO);
        suggestionsBuilder.text(suggestQuery.getText());
        suggestionsBuilder.field(suggestQuery.getField());
        suggestionsBuilder.size(suggestQuery.getSize());

        SuggestRequestBuilder suggestRequestBuilder = ESClient.getClient()
                .prepareSuggest(indices.split(",")).addSuggestion(suggestionsBuilder);
        SuggestResponse resp = suggestRequestBuilder.execute().actionGet();
        // 查询结果
        List<? extends Entry<? extends Option>> entries = (List<? extends Entry<? extends Option>>) resp
                .getSuggest().getSuggestion(suggestQuery.getSuggestname()).getEntries();
        if (entries == null) {
            return Lists.newArrayList();
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Entry<? extends Option> e : entries) {
            for (Option option : e) {
                Map<String, Object> map_payload = option.getPayloadAsMap();
                map_payload.put("NAME", option.getText().toString());
                result.add(map_payload);
            }
        }

        // 调试模式
        if (Constants.isDebug) {
            logger.info(suggestionsBuilder.toString());
            logger.info(JsonUtil.toJson(result));
        }
        return result;
    }

}
