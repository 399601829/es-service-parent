package com.es.service.index.dao.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface Mapper<T> {

    /**
     * 查询全量
     * 
     * @param startIndex
     * @param pageSize
     * @return
     */
    List<T> listAll(@Param("startId") Integer startIndex, @Param("pageSize") Integer pageSize);

    /**
     * 查询增量
     * 
     * @param lastIndexTime
     * @param pageSize
     * @return
     */
    List<T> listIncrement(@Param("date") Date lastIndexTime,
            @Param("startId") Integer startIndex, @Param("pageSize") Integer pageSize);

    /**
     * 查询单个对象
     * 
     * @param key
     * @return
     */
    T query(@Param("key") Object key);

}
