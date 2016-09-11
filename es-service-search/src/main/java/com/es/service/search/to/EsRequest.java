package com.es.service.search.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.es.service.common.type.IndexType;
import com.es.service.search.type.ConditionType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * 搜索请求体
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月31日
 * 
 */
public class EsRequest implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3847009868261011550L;

    /**
     * 索引名称
     */
    private String indexname;

    /**
     * type集合
     */
    private Set<String> types;

    /**
     * 过滤脚本
     */
    private List<EsFilterScript> filterScripts;

    /**
     * 得分脚本
     */
    private List<ScoreScript> scoreScripts;

    /**
     * 页码
     */
    private int pn;

    /**
     * 页大小
     */
    private int ps;

    /**
     * 需要返回的列
     */
    private List<String> resultFileds;
    
    /**
     * 需要排序的列
     */
    private List<String> sortFileds;

    /**
     * 高亮
     */
    private EsHighlightFields highlightFields;

    /**
     * 搜索条件
     */
    private Map<SearchCondition, ConditionType> searchConditions;

    /**
     * 扩展字段
     */
    private Map<String, String> extend;

    /**
     * 关键字
     */
    private String keyword;

    public EsRequest() {

    }

    /**
     * 构造方法
     * 
     * @param indexType 搜索的索引
     * @param pn
     * @param ps
     * @throws Exception
     */
    public EsRequest(IndexType indexType, int pn, int ps) {
        this(indexType, pn, ps, null);
    }

    /**
     * 构造方法
     * 
     * @param indexType 搜索的索引
     * @param pn
     * @param ps
     * @param types type列表
     * @throws Exception
     */
    public EsRequest(IndexType indexType, int pn, int ps, IndexType... types) {
        this.pn = pn > 0 ? pn : 1;
        this.ps = ps > 0 ? ps : 10;
        this.indexname = indexType.getIndexName();
        searchConditions = new HashMap<SearchCondition, ConditionType>();
        this.types = Sets.newHashSet();
        if (types != null && types.length > 0) {
            for (IndexType type : types) {
                this.types.add(type.getTypeName());
            }
        }
    }

    /**
     * and 查询
     * 
     * @param searchcondition
     */
    public void andSearchCondition(SearchCondition searchcondition) {
        this.searchConditions.put(searchcondition, ConditionType.AND);
    }

    /**
     * or 查询
     * 
     * @param searchcondition
     */
    public void orSearchCondition(SearchCondition searchcondition) {
        this.searchConditions.put(searchcondition, ConditionType.OR);
    }

    /**
     * 获取全部查询值
     * 
     * @return
     */
    public Set<String> getAllValue() {
        Set<String> sets = new HashSet<String>();
        try {
            if (searchConditions != null) {
                Set<SearchCondition> sConditions = searchConditions.keySet();
                for (SearchCondition c : sConditions) {
                    sets.addAll(buildQueryKeys(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sets;
    }

    /**
     * 获取SearchCondition的value
     * 
     * @param searchCondition
     * @return
     */
    public Set<String> buildQueryKeys(SearchCondition searchCondition) {
        Set<String> sets = new HashSet<String>();
        if (searchCondition.hasSubquery()) {
            List<SearchCondition> sConditions = searchCondition.getSubquery();
            for (SearchCondition c : sConditions) {
                Set<String> subSet = buildQueryKeys(c);
                sets.addAll(subSet);
            }
        } else if (searchCondition != null) {
            sets.addAll(searchCondition.getAllValues());
        }
        return sets;
    }

    /**
     * 需要返回的列，不设置返回全部
     * 
     * @return
     */
    public List<String> getSafeResultFileds() {
        if (resultFileds == null) {
            resultFileds = new ArrayList<String>();
        }
        return resultFileds;
    }
    /**
     * 需要排序的列
     * 
     * @return
     */
    public List<String> getSafeSortFileds() {
        if (sortFileds == null) {
            sortFileds = new ArrayList<String>();
        }
        return sortFileds;
    }

    /**
     * 获取es查询结果过滤脚本
     * 
     * @return
     */
    public List<EsFilterScript> getSafeFilterScript() {
        if (filterScripts == null) {
            filterScripts = new ArrayList<EsFilterScript>();
        }
        return filterScripts;
    }

    /**
     * 获取es得分脚本
     * 
     * @return
     */
    public List<ScoreScript> getSafeScoreScript() {
        if (scoreScripts == null) {
            scoreScripts = new ArrayList<ScoreScript>();
        }
        return scoreScripts;
    }

    /**
     * @return the extend
     */
    public Map<String, String> getSafeExtend() {
        if (extend == null) {
            extend = Maps.newHashMap();
        }
        return extend;
    }

    /**
     * @return the indexname
     */
    public String getIndexname() {
        return indexname;
    }

    /**
     * @return the types
     */
    public Set<String> getTypes() {
        return types;
    }

    /**
     * @return the pn
     */
    public int getPn() {
        return pn;
    }

    /**
     * @return the ps
     */
    public int getPs() {
        return ps;
    }

    /**
     * @return the searchConditions
     */
    public Map<SearchCondition, ConditionType> getSearchConditions() {
        return searchConditions;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the highlightFields
     */
    public EsHighlightFields getSafeHighlightFields() {
        if (highlightFields == null) {
            highlightFields = new EsHighlightFields();
        }
        return highlightFields;
    }

    /**
     * @param highlightFields the highlightFields to set
     */
    public void setHighlightFields(EsHighlightFields highlightFields) {
        this.highlightFields = highlightFields;
    }
}
