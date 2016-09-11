package com.es.service.search.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月4日
 *
 */
public class EsSort {

    /**
     * 
     * @param srb
     * @param sort_fileds
     * @return
     */
    public static void sortAdapter(SearchRequestBuilder srb, List<String> sort_fileds) {
        // 处理排序
        for (SortBuilder s : sortAdapter(sort_fileds)) {
            srb.addSort(s);
        }
    }

    /**
     * 
     * @param sort_fileds
     * @return
     */
    public static List<SortBuilder> sortAdapter(List<String> sort_fileds) {
        List<SortBuilder> sortBuilders = new ArrayList<SortBuilder>();
        for (String sort : sort_fileds) {
            String[] items = StringUtils.split(sort, " ");
            if (items.length > 2 || items.length < 2) {
                throw new RuntimeException("排序参数格式不正确，必须为：filed desc|asc,多个filed以逗号分隔！");
            }
            String[] fileds = items[0].split(",");
            for (String filed : fileds) {
                SortBuilder sortb = null;
                if (items[0].equalsIgnoreCase("desc")) {
                    sortb = SortBuilders.fieldSort(filed).order(SortOrder.DESC);
                } else {
                    sortb = SortBuilders.fieldSort(filed).order(SortOrder.ASC);
                }
                sortBuilders.add(sortb);
            }

        }
        return sortBuilders;
    }
}
