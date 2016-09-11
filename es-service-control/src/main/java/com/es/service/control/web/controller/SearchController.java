package com.es.service.control.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.es.service.common.util.JsonUtil;
import com.es.service.control.service.SearchService;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月6日
 * 
 */
@Controller
@RequestMapping
public class SearchController {

    @Resource
    private SearchService searchService;

    @ResponseBody
    @RequestMapping("/search")
    public String search(String indexName, String typeName, String keyWord) {
        try {
            keyWord = new String(keyWord.getBytes("iso8859-1"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return searchService.search(indexName, typeName, keyWord);
    }
}
