package com.es.service.index.core.reindexlog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.conf.Constants;
import com.es.service.common.type.IndexType;

/**
 * 
 * 写文件的方式，如果多节点部署需要对文件挂载nfs
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月19日
 * 
 */
public class IndexUpdateFileLogger implements IndexUpdateLogger {

    private static final Logger log = LoggerFactory.getLogger(IndexUpdateFileLogger.class);

    /**
     * 
     */
    private final Logger LOGGER = LoggerFactory.getLogger(IndexUpdateFileLogger.class);

    /**
     * 记录索引更新信息的文件名
     */
    private final String INDEX_INFO_FILE = "index_update_%d.properties";

    /**
     * 索引最后更新时间
     */
    private final String KEY_LAST_INDEX_TIME = "lastIndexTime";

    /**
     * 索引最后更新的真实索引名
     */
    private final String KEY_LAST_INDEX_NAME = "lastindexname";

    /**
     * 索引信息文件存放目录
     */
    private final String INDEX_INFO_DIR = Constants.es_index_info_dir;

    @Override
    public Long getLastIndexTime(IndexType indexType, long specifiedTime) {
        if (specifiedTime > 0) {
            return specifiedTime;
        }
        Properties indexInfo = new Properties();
        FileInputStream in = null;
        try {
            String path = INDEX_INFO_DIR + String.format(INDEX_INFO_FILE, indexType.getIndexNo());
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            in = new FileInputStream(new File(path));
            indexInfo.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long lastIndexTime = Long.valueOf(indexInfo.getProperty(KEY_LAST_INDEX_TIME));
        if (lastIndexTime == 0) {
            throw new RuntimeException(String.format("第一次的索引更新还没有完成,索引名称：%s",
                    indexType.getIndexName()));
        }
        LOGGER.info("last index time: {}={}", KEY_LAST_INDEX_TIME + "_" + indexType.getIndexNo(),
                new Date(lastIndexTime));
        return lastIndexTime;
    }

    @Override
    public String getLastIndexName(IndexType indexType) {
        Properties indexInfo = new Properties();
        FileInputStream in = null;
        try {
            String path = INDEX_INFO_DIR + String.format(INDEX_INFO_FILE, indexType.getIndexNo());
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            in = new FileInputStream(new File(path));
            indexInfo.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return indexInfo.getProperty(KEY_LAST_INDEX_NAME);
    }

    @Override
    public void log(IndexType indexType, String indexName, long indexTime) {
        Properties indexInfo = new Properties();
        FileOutputStream out = null;
        try {
            String path = INDEX_INFO_DIR + String.format(INDEX_INFO_FILE, indexType.getIndexNo());
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(path);
            indexInfo.put(KEY_LAST_INDEX_TIME, String.valueOf(indexTime));
            indexInfo.put(KEY_LAST_INDEX_NAME, indexName);
            indexInfo.store(out, null);

            log.error("record index update log!.indexName:{},indexTime:{}", indexName, indexName);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("record index update log error!.indexName:{},indexTime:{}", indexName,
                    indexName);
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
