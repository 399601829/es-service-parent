package com.es.service.search.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;

import com.es.service.search.to.EsHighlightFields;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月4日
 *
 */
public class EsHighLight {

    /**
     * 
     * @param srb
     * @param highlightFields
     */
    public static void setHighLight(SearchRequestBuilder srb, EsHighlightFields highlightFields) {
        if (highlightFields == null || highlightFields.getFields() == null) {
            return;
        }
        srb.setHighlighterPreTags(highlightFields.getPreTags());
        srb.setHighlighterPostTags(highlightFields.getPostTags());
        srb.setHighlighterRequireFieldMatch(highlightFields.getRequireFieldMatch());
        for (String word : highlightFields.getFields()) {
            srb.addHighlightedField(word, highlightFields.getFragmentSize(),
                    highlightFields.getNumOfFragments());
        }
    }

    /**
     * 
     * @param hit
     * @param seach_fileds
     * @return
     */
    public static Map<String, Object> getHighlight(SearchHit hit, List<String> seach_fileds) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, HighlightField> highlights = hit.highlightFields();
        for (String filed : seach_fileds) {
            HighlightField highlight = highlights.get(filed);
            if (null == highlight) {
                continue;
            }
            StringBuffer sb = new StringBuffer();
            Text[] fragments = highlight.fragments();
            for (Text fragment : fragments) {
                sb.append(fragment);
            }
            result.put(filed + "_HIGH", sb.toString());
        }
        return result;

    }
}
