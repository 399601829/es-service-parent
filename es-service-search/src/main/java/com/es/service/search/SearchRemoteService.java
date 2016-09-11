package com.es.service.search;

import java.util.concurrent.TimeUnit;

import com.es.service.search.to.EsRequest;
import com.es.service.search.to.EsResponse;

/**
 * 
 * 搜索接口
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月29日
 * 
 */
public interface SearchRemoteService {

    /**
     * 搜索接口
     * 
     * @param request 请求体
     * @param timeout 超时时间
     * @param timeUnit 时间单位
     * @return
     */
    EsResponse search(EsRequest request, int timeout, TimeUnit timeUnit);

    /**
     * 建议器搜索，返回固定的建议内容
     * 
     * @param request
     * @return
     */
    EsResponse suggestSearch(EsRequest request);
}
