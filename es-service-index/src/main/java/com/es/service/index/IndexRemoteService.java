package com.es.service.index;

import com.es.service.common.type.Event;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月24日
 * 
 */
public interface IndexRemoteService {

    /**
     * 监控接口
     * 
     * @return String
     */
    String monitor();

    /**
     * 更新索引
     * 
     * @param <T>
     * 
     * @param event
     * @return
     */
    <T> boolean updateIndex(Event<T> event);

}
