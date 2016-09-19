package com.es.service.control.service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.es.service.common.type.IndexType;
import com.es.service.common.util.JsonUtil;
import com.es.service.control.service.SearchService;
import com.es.service.search.SearchRemoteService;
import com.es.service.search.to.EsFilterScript;
import com.es.service.search.to.EsRequest;
import com.es.service.search.to.EsResponse;
import com.es.service.search.to.ScoreScript;
import com.es.service.search.to.SearchCondition;
import com.es.service.search.type.ConditionType;
import com.es.service.search.type.PinyinType;
import com.es.service.search.type.SearchType;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月6日
 * 
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private SearchRemoteService remoteService;

    @Override
    public String search(String indexName, String typeName, String keyWord) {

        SearchCondition term = new SearchCondition(SearchType.TERM, ConditionType.OR);
        term.add("NAME", keyWord);
        term.add("ENNAME", keyWord);

        SearchCondition match = new SearchCondition(SearchType.MATCHING_PHRASE, ConditionType.OR);
        match.add("NAME_QUERY", keyWord, true);

        SearchCondition fuzz = new SearchCondition(SearchType.FUZZ, ConditionType.OR);
        fuzz.add("NAME_QUERY", keyWord, true);
        fuzz.add("ENNAME_QUERY", keyWord);

        SearchCondition query_string = new SearchCondition(SearchType.QUERY_STRING,
                ConditionType.OR);
        query_string.add("NAME_QUERY", keyWord);
        query_string.add("NAME_PINYIN_QUERY", keyWord, PinyinType.PINYIN_ALL);

        EsRequest request = new EsRequest(IndexType.RESOURCES, 1, 10);
        request.orSearchCondition(term);
        // request.orSearchCondition(prefix);
        request.orSearchCondition(match);
        request.orSearchCondition(fuzz);
        request.orSearchCondition(query_string);

        // 修改得分
        ScoreScript score = new ScoreScript("_score+1");
        request.getSafeScoreScript().add(score);

        // 过滤
        EsFilterScript filter = new EsFilterScript("doc['STATUS'].value == 1");
        request.getSafeFilterScript().add(filter);

        // 返回字段
        request.getSafeResultFileds().add("NAME");

        // 高亮
        request.getSafeHighlightFields().addField("NAME").addField("NAME_QUERY");

        EsResponse response = remoteService.search(request, 10, TimeUnit.SECONDS);

        return JsonUtil.toJson(response);
    }

    @Override
    public String suggest(String indexName, String keyWord) {
        EsRequest request = new EsRequest(IndexType.RESOURCES);
        request.getSafeSuggestQuery().setText(keyWord);
        EsResponse immediateSearch = remoteService.suggestSearch(request);
        return immediateSearch.getJsonObject();
    }
}
