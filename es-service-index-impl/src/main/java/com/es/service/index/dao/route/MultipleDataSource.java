package com.es.service.index.dao.route;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 
 * 数据源路由器，根据dataSourceKey路由到指定数据源
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
public class MultipleDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> value = new InheritableThreadLocal<String>();

    public static void setDataSourceKey(String dataSource) {
        value.set(dataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String ds = value.get();
        value.remove();
        return ds;
    }

}