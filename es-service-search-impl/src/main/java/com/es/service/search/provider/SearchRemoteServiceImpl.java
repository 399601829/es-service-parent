package com.es.service.search.provider;


import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.es.service.search.SearchRemoteService;
import com.es.service.search.engine.EsSearch;
import com.es.service.search.to.EsRequest;
import com.es.service.search.to.EsResponse;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月31日
 *
 */
@Service("searchRemoteService")
public class SearchRemoteServiceImpl implements SearchRemoteService {

    @Override
    public EsResponse search(EsRequest request, int timeout, TimeUnit timeUnit) {
        return EsSearch.search(request, timeout, timeUnit);
    }

    @Override
    public EsResponse suggestSearch(EsRequest request) {
        return EsSearch.suggestSearch(request);
    }
    
}
