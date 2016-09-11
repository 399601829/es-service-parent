package com.es.service.search.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import com.es.service.search.type.ConditionType;
import com.es.service.search.type.PinyinType;
import com.es.service.search.type.SearchType;
import com.es.service.search.util.KeyWordUtil;
import com.google.common.collect.Lists;

/**
 * es查询组合
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月30日
 * 
 */
public final class SearchCondition implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6182197530062224990L;

    /**
     * 多个具体查询条件
     */
    private List<Condition> conditions = new ArrayList<Condition>();

    /**
     * 查询条件之间的关系
     */
    private ConditionType conditionType;

    /**
     * 对应于es查询类型
     */
    private SearchType searchType;

    /**
     * 子查询
     */
    private List<SearchCondition> subquery = new ArrayList<SearchCondition>();

    /**
     * 子查询的多种查询组合方式
     */
    private ConditionType subQueryType = ConditionType.AND;

    /**
     * 是否有父引用
     */
    private boolean isParent;

    
    
    /**
     * 
     */
    public SearchCondition() {
        super();
    }

    public SearchCondition(SearchType searchType, ConditionType conditionType) {
        super();
        this.searchType = searchType;
        this.conditionType = conditionType;
    }

    /**
     * add
     * 
     * @param field
     * @param value
     * @return
     */
    public SearchCondition add(Object field, Object value) {
        this.conditions.add(new Condition(String.valueOf(field), String.valueOf(value)));
        return this;
    }

    /**
     * add
     * 
     * @param field
     * @param value
     * @param boost
     * @return
     */
    public SearchCondition add(Object field, Object value, float boost) {
        this.conditions.add(new Condition(String.valueOf(field), String.valueOf(value), boost));
        return this;
    }

    /**
     * add-同时处理空格拆分
     * 
     * @param field
     * @param value
     * @return
     */
    public SearchCondition add(Object field, Object value, boolean isSpaceSplit) {
        this.conditions.add(new Condition(String.valueOf(field), String.valueOf(value),
                isSpaceSplit));
        return this;
    }

    /**
     * add
     * 
     * @param field
     * @param value
     * @return
     */
    public SearchCondition add(Object field, Object value, PinyinType valueConvertPinyin) {
        this.conditions.add(new Condition(String.valueOf(field), String.valueOf(value),
                valueConvertPinyin));
        return this;
    }

    /**
     * addAll
     * 
     * @param conditionMap
     * @return
     */
    public SearchCondition addAll(Map<String, String> conditionMap) {
        if (conditionMap != null) {
            Set<String> keys = conditionMap.keySet();
            for (String key : keys) {
                this.add(key, conditionMap.get(key));
            }
        }
        return this;
    }

    /**
     * 获取所有的值
     * 
     * @return
     */
    public List<String> getAllValues() {
        List<String> values = new ArrayList<String>();
        for (Condition condition : conditions) {
            values.addAll(KeyWordUtil.processKeyWord(condition.getValue()));
        }
        return values;
    }

    /**
     * 添加子查询-and
     * 
     * @param esSearchCondition
     * @return
     */
    public SearchCondition andSubquery(SearchCondition searchCondition) {
        if (searchCondition.isParent) {
            throw new RuntimeException("只支持一级子查询，防止嵌套过多！");
        }
        searchCondition.isParent = true;
        this.subquery.add(searchCondition);
        this.subQueryType = ConditionType.AND;
        return this;
    }

    /**
     * 添加子查询-or
     * 
     * @param esSearchCondition
     * @return
     */
    public SearchCondition orSubquery(SearchCondition searchCondition) {
        if (searchCondition.isParent) {
            throw new RuntimeException("只支持一级子查询，防止嵌套过多！");
        }
        searchCondition.isParent = true;
        this.subquery.add(searchCondition);
        this.subQueryType = ConditionType.OR;
        return this;
    }

    /**
     * 是否有子查询
     * 
     */
    public boolean hasSubquery() {
        if (subquery != null && subquery.size() > 0) {
            return true;
        }
        return false;
    }

    public List<SearchCondition> getSubquery() {
        return subquery;
    }

    public ConditionType getSubQueryType() {
        return subQueryType;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

}
