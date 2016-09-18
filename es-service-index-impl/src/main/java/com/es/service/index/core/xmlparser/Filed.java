package com.es.service.index.core.xmlparser;

import java.util.List;

/**
 * 
 * 字段
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月15日
 * 
 */
public class Filed {

    private int id;

    // 字段名称
    private String name;

    // 数据库字段类型
    private String type;

    // 存储字段类型
    private String storeType;

    // 是否存储
    private boolean isstore = true;

    // 是否索引
    private boolean isindex = true;

    // 是否分词
    private boolean isAnalyzer = false;

    // 索引分词器
    private boolean indexAnalyzer;

    // 搜索分词器
    private boolean searchAnalyzer;

    // 分词方式
    private String analyzer = "ik_max_word";

    // 是否默认搜索(加入_all字段)
    private boolean isdefaultsearch = false;

    // 是否主键
    private boolean isuniqueKey;

    // 多值字段-偏差值
    private int position_offset_gap;

    // 是否copy字段
    private boolean iscopy;

    // copy字段
    private String copyto;

    // copy字段内容
    private List<Filed> names;

    // 权重
    private double weight;

    // 时间格式
    private String format;

    private boolean isUpperCase = true;

    public String getNameToUpperCase() {
        if (isUpperCase) {
            return name.toUpperCase();
        } else {
            return name;
        }
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the storeType
     */
    public String getStoreType() {
        return storeType;
    }

    /**
     * @param storeType the storeType to set
     */
    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    /**
     * @return the isstore
     */
    public boolean isIsstore() {
        return isstore;
    }

    /**
     * @param isstore the isstore to set
     */
    public void setIsstore(boolean isstore) {
        this.isstore = isstore;
    }

    /**
     * @return the isindex
     */
    public boolean isIsindex() {
        return isindex;
    }

    /**
     * @param isindex the isindex to set
     */
    public void setIsindex(boolean isindex) {
        this.isindex = isindex;
    }

    /**
     * @return the isAnalyzer
     */
    public boolean isAnalyzer() {
        return isAnalyzer;
    }

    /**
     * @param isAnalyzer the isAnalyzer to set
     */
    public void setAnalyzer(boolean isAnalyzer) {
        this.isAnalyzer = isAnalyzer;
    }

    /**
     * @return the indexAnalyzer
     */
    public boolean isIndexAnalyzer() {
        return indexAnalyzer;
    }

    /**
     * @param indexAnalyzer the indexAnalyzer to set
     */
    public void setIndexAnalyzer(boolean indexAnalyzer) {
        this.indexAnalyzer = indexAnalyzer;
    }

    /**
     * @return the searchAnalyzer
     */
    public boolean isSearchAnalyzer() {
        return searchAnalyzer;
    }

    /**
     * @param searchAnalyzer the searchAnalyzer to set
     */
    public void setSearchAnalyzer(boolean searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }

    /**
     * @return the analyzer
     */
    public String getAnalyzer() {
        return analyzer;
    }

    /**
     * @param analyzer the analyzer to set
     */
    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * @return the isdefaultsearch
     */
    public boolean isIsdefaultsearch() {
        return isdefaultsearch;
    }

    /**
     * @param isdefaultsearch the isdefaultsearch to set
     */
    public void setIsdefaultsearch(boolean isdefaultsearch) {
        this.isdefaultsearch = isdefaultsearch;
    }

    /**
     * @return the isuniqueKey
     */
    public boolean isIsuniqueKey() {
        return isuniqueKey;
    }

    /**
     * @param isuniqueKey the isuniqueKey to set
     */
    public void setIsuniqueKey(boolean isuniqueKey) {
        this.isuniqueKey = isuniqueKey;
    }

    /**
     * @return the position_offset_gap
     */
    public int getPosition_offset_gap() {
        return position_offset_gap;
    }

    /**
     * @param position_offset_gap the position_offset_gap to set
     */
    public void setPosition_offset_gap(int position_offset_gap) {
        this.position_offset_gap = position_offset_gap;
    }

    /**
     * @return the iscopy
     */
    public boolean isIscopy() {
        return iscopy;
    }

    /**
     * @param iscopy the iscopy to set
     */
    public void setIscopy(boolean iscopy) {
        this.iscopy = iscopy;
    }

    /**
     * @return the copyto
     */
    public String getCopyto() {
        return copyto;
    }

    /**
     * @param copyto the copyto to set
     */
    public void setCopyto(String copyto) {
        this.copyto = copyto.toUpperCase();
    }

    /**
     * @return the names
     */
    public List<Filed> getNames() {
        return names;
    }

    /**
     * @param names the names to set
     */
    public void setNames(List<Filed> names) {
        this.names = names;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

}
