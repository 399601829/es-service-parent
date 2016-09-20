package com.es.service.index.service;

import java.util.List;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月20日
 *
 */
public interface IndexService<T> {

    int doFullIndex();

    int doIncreamentIndex();

    int doIncreamentIndex(long specifiedTime);

    void delDoc(String docId);

    void updateDoc(T to);

    void updateDoc(List<T> tos);
}
