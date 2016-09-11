package com.es.service.index.core;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.type.IndexType;
import com.es.service.common.util.JsonUtil;
import com.es.service.index.bean.BaseTO;
import com.es.service.index.common.type.IndexServiceMapping;
import com.es.service.index.core.reindexlog.IndexUpdateFileLogger;
import com.es.service.index.core.reindexlog.IndexUpdateLogger;
import com.es.service.index.dao.mapper.Mapper;

/**
 * 
 * 索引服务支持
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月19日
 * 
 */
public abstract class IndexServiceSupport<T extends BaseTO> {

    /**
     * 
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 日志记录器
     */
    protected IndexUpdateLogger updateLogger = new IndexUpdateFileLogger();

    /**
     * 数据源
     */
    protected Mapper<T> mapper;

    /**
     * 索引
     */
    protected IndexType indexType;

    protected Class<T> clazz;

    /**
     * 
     */
    public IndexServiceSupport() {
        this.clazz = null;
        Class<?> c = getClass();
        Type type = c.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            // 获取下标为0的泛型
            this.clazz = (Class<T>) parameterizedType[0];
        }
    }

    /**
     * @return the mapper
     */
    protected Mapper<T> getMapper() {
        if (mapper == null) {
            IndexServiceMapping indexMapping = IndexServiceMapping.getIndexServiceMapping(clazz);
            mapper = indexMapping.getMapper();
        }
        return mapper;
    }

    /**
     * @return the indexType
     */
    protected IndexType getIndexType() {
        if (indexType == null) {
            IndexServiceMapping indexMapping = IndexServiceMapping.getIndexServiceMapping(clazz);
            indexType = indexMapping.getIndexType();
        }
        return indexType;
    }

    /**
     * 全量索引
     * 
     * @return
     * @throws IOException
     */
    protected int doFullIndex() {
        try {
            int startIndex = 0;
            int indexCount = 0;
            int pageSize = 1000;
            long indexTime = System.currentTimeMillis();
            List<T> listAll = null;
            IndexDispatcher dispatcher = IndexDispatcher.createDispatcher(getIndexType(), true);

            // 添加索引数据
            do {
                listAll = getMapper().listAll(startIndex, pageSize);
                if (listAll != null && listAll.size() > 0) {
                    for (T res : listAll) {
                        dispatcher.indexed(JsonUtil.toJson(res));
                    }
                    indexCount += listAll.size();
                    startIndex = listAll.get(listAll.size() - 1).getId();
                }
            } while (listAll != null && listAll.size() > 0);

            // 提交索引
            String currentIndexName = dispatcher.commit();
            updateLogger.log(getIndexType(), currentIndexName, indexTime);
            log.info("doFullIndex is successful! indexname:{},indexCount:{},indexTime:{}",
                    currentIndexName, indexCount, indexTime);
        } catch (Exception e) {
            log.error("doFullIndex is error!", e);
        }
        return 0;
    }

    /**
     * 增量索引
     * 
     * @return
     */
    public int doIncreamentIndex() {
        try {
            return doIncreamentIndex(0);
        } catch (Exception e) {
            log.error("doIncreamentIndex is error!", e);
        }
        return 0;
    }

    /**
     * 根据指定的时间建增量索引
     * 
     * @param specifiedTime
     * @return
     * @throws IOException
     */
    protected int doIncreamentIndex(long specifiedTime) {
        int startIndex = 0;
        int pageSize = 1000;
        long indexTime = System.currentTimeMillis();
        List<T> listAll = null;
        long lastIndexTime = updateLogger.getLastIndexTime(getIndexType(), specifiedTime);
        IndexDispatcher dispatcher = IndexDispatcher.createDispatcher(getIndexType(), false);
        // 添加索引数据
        do {
            listAll = getMapper().listIncrement(new Date(lastIndexTime), startIndex, pageSize);
            if (listAll != null && listAll.size() > 0) {
                for (T res : listAll) {
                    dispatcher.indexed(JsonUtil.toJson(res));
                }
            }
        } while (listAll != null && listAll.size() > 0);

        // 提交索引
        String currentIndexName = dispatcher.commit();
        updateLogger.log(getIndexType(), currentIndexName, indexTime);
        return 0;
    }

    /**
     * 更新doc
     */
    public void updateDoc(T to) {
        IndexDispatcher dispatcher = IndexDispatcher.createDispatcher(getIndexType(), false);
        dispatcher.indexed(JsonUtil.toJson(to));
        dispatcher.commit();
    }
    
    /**
     * 更新doc
     */
    public void updateDoc(String jsonObject) {
        IndexDispatcher dispatcher = IndexDispatcher.createDispatcher(getIndexType(), false);
        dispatcher.indexed(jsonObject);
        dispatcher.commit();
    }

    /**
     * 更新doc
     */
    public void updateDoc(List<T> tos) {
        IndexDispatcher dispatcher = IndexDispatcher.createDispatcher(getIndexType(), false);
        dispatcher.indexedList(tos);
        dispatcher.commit();
    }

    /**
     * 删除doc
     */
    public void delDoc(String docId) {
        IndexDispatcher dispatcher = IndexDispatcher.createDispatcher(getIndexType(), false);
        dispatcher.delDoc(docId);
    }

}
