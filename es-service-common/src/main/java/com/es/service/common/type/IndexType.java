/**
 * 
 */
package com.es.service.common.type;

import java.util.Arrays;
import java.util.List;

/**
 * 索引类型
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月16日
 * 
 */
public enum IndexType {

    RESOURCES(1, "index", "resources", "游戏数据");

    /**
     * 索引编号
     */
    private int indexNo;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 描述
     */
    private String description;

    private IndexType(int indexNo, String indexName, String typeName, String description) {
        this.indexNo = indexNo;
        this.indexName = indexName;
        this.typeName = typeName;
        this.description = description;
    }

    /**
     * @return the indexNo
     */
    public int getIndexNo() {
        return indexNo;
    }

    /**
     * @param indexNo the indexNo to set
     */
    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
    }

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
     * @param type the typeName to set
     */
    public void setTypeName(String type) {
        this.typeName = type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取index_type的组合名称，即索引的使用名称-别名
     * 
     * @return
     */
    public String index_type() {
        return this.indexName + "_" + this.typeName;
    }

    /**
     * 获取index_type_1的组合名称，重建索引时切换索引名称
     * 
     * @return
     */
    public String index_type_1() {
        return this.indexName + "_" + this.typeName + "_1";
    }

    /**
     * 获取index_type_2的组合名称，重建索引时切换索引名称
     * 
     * @return
     */
    public String index_type_2() {
        return this.indexName + "_" + this.typeName + "_2";
    }

    /**
     * 全部类型
     * 
     * @return
     */
    public static List<IndexType> getAllIndex() {
        return Arrays.asList(IndexType.values());
    }

    /**
     * 查找对应的索引类型
     * 
     * @param indexName
     * @param typeName
     * @return
     */
    public static IndexType getIndexTypeByIndexNameAndType(String indexName, String typeName) {
        IndexType[] indexTypes = IndexType.values();
        for (IndexType indexType : indexTypes) {
            if (indexType.indexName.equals(indexName) && indexType.typeName.equals(typeName)) {
                return indexType;
            }
        }
        return null;
    }

    public static IndexType getIndexType(String indexName) {
        IndexType[] indexTypes = IndexType.values();
        for (IndexType indexType : indexTypes) {
            if (indexType.indexName.equals(indexName)) {
                return indexType;
            }
        }
        return RESOURCES;
    }
}
