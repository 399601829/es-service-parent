package com.es.service.index.dao.route;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceRoute {

    DataSource value() default DataSource.RESOURCES;

}
