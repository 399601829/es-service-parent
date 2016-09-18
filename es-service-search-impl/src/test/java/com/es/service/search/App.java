package com.es.service.search;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.es.service.common.type.IndexType;
import com.es.service.search.engine.EsSearch;
import com.es.service.search.to.EsRequest;
import com.es.service.search.to.EsResponse;
import com.es.service.search.to.EsFilterScript;
import com.es.service.search.to.ScoreScript;
import com.es.service.search.to.SearchCondition;
import com.es.service.search.type.ConditionType;
import com.es.service.search.type.PinyinType;
import com.es.service.search.type.SearchType;

/**
 * 测试搜索
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月4日
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-conf/es-service-search-provider.xml" })
public class App {

    @Resource
    private SearchRemoteService service;

    @Test
    public void test1() {
        //String keyWord = "请问我的世界好玩吗";
        //String keyWord = "我的世界好玩吗";
        //String keyWord = "我的世界 好玩吗";
        //String keyWord = "我的世界 战争";
        //String keyWord = "我的世";
        //String keyWord = "wodeshijie";
        //String keyWord = "wodeshi";
        //String keyWord = "wds";
        //String keyWord = "wdsj";
        String keyWord = "我的世界";
        SearchCondition term = new SearchCondition(SearchType.TERM, ConditionType.OR);
        term.add("NAME", keyWord);
        term.add("ENNAME", keyWord);

        // SearchCondition prefix = new SearchCondition(SearchType.PREFIX, ConditionType.OR);
        // prefix.add("NAME_QUERY", keyWord);
        // prefix.add("ENNAME", keyWord);

        SearchCondition match = new SearchCondition(SearchType.MATCHING_PHRASE, ConditionType.OR);
        match.add("NAME_QUERY", keyWord, true);

        SearchCondition fuzz = new SearchCondition(SearchType.FUZZ, ConditionType.OR);
        fuzz.add("NAME_QUERY", keyWord,true);
        fuzz.add("ENNAME_QUERY", keyWord);

        SearchCondition query_string = new SearchCondition(SearchType.QUERY_STRING,
                ConditionType.OR);
        query_string.add("NAME_QUERY", keyWord);
        query_string.add("NAME_PINYIN_QUERY", keyWord,PinyinType.PINYIN_ALL);

        EsRequest request = new EsRequest(IndexType.RESOURCES, 1, 10);
        request.orSearchCondition(term);
        // request.orSearchCondition(prefix);
        request.orSearchCondition(match);
        request.orSearchCondition(fuzz);
        request.orSearchCondition(query_string);

        // 修改得分
        ScoreScript score = new ScoreScript("_score+1");
        //request.getSafeScoreScript().add(score);

        // 过滤
        EsFilterScript filter = new EsFilterScript("doc['STATUS'].value == 1");
        request.getSafeFilterScript().add(filter);

        // 返回字段
        request.getSafeResultFileds().add("NAME");

        // 高亮
        request.getSafeHighlightFields().addField("NAME").addField("NAME_QUERY");

        EsResponse response = service.search(request, 1, TimeUnit.SECONDS);
        System.out.println(response.getJsonObject());
    }

    @Test
    public void test2() {

        EsRequest request = new EsRequest(IndexType.RESOURCES);
        request.getSafeSuggestQuery().setText("大话");
        
        EsResponse immediateSearch = EsSearch.suggestSearch(request);
        System.out.println(immediateSearch);
    }

}
