package com.es.service.index.dao.mapper;

import org.springframework.stereotype.Repository;

import com.es.service.index.bean.ResourcesTO;
import com.es.service.index.dao.route.DataSource;
import com.es.service.index.dao.route.DataSourceRoute;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
@DataSourceRoute(DataSource.RESOURCES)
@Repository("resourcesMapper")
public interface ResourcesMapper extends Mapper<ResourcesTO>{


}
