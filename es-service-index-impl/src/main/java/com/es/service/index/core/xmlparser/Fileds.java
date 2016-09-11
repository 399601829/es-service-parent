package com.es.service.index.core.xmlparser;

import java.util.List;

/**
 * 
 * 完整的配置文档
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月15日
 * 
 */
public class Fileds {

    /**
     * indexName
     * 
     */
    private String indexName;
    /**
     * typeName
     * 
     */
    private String typeName;

    /**
     * uniqueKey
     */
    private String key;

    /**
     * Filed
     */
    private List<Filed> listfiled;

    /**
     * 建议器
     */
    private boolean issuggest = false;

    /**
     * @return the indexName
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * @param indexName the indexName to set
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the listfiled
     */
    public List<Filed> getListfiled() {
        return listfiled;
    }

    /**
     * @param listfiled the listfiled to set
     */
    public void setListfiled(List<Filed> listfiled) {
        this.listfiled = listfiled;
    }

    /**
     * @return the issuggest
     */
    public boolean isIssuggest() {
        return issuggest;
    }

    /**
     * @param issuggest the issuggest to set
     */
    public void setIssuggest(boolean issuggest) {
        this.issuggest = issuggest;
    }

}
