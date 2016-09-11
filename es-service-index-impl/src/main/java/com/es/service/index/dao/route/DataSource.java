package com.es.service.index.dao.route;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
public enum DataSource {

    RESOURCES("resources", "资源");

    private String key;
    private String description;

    /**
     * @param key
     * @param description
     */
    private DataSource(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

}
