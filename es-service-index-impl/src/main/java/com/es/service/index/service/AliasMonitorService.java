package com.es.service.index.service;

import java.util.List;

import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.es.service.common.client.ESClient;
import com.es.service.common.type.IndexType;
import com.es.service.index.core.reindexlog.IndexUpdateFileLogger;
import com.es.service.index.core.reindexlog.IndexUpdateLogger;

/**
 * 
 * 别名监控服务，主要检查，重建索引时旧索引删除失败，导致一个别名指向两个相同的索引
 * <p>
 * index_type_1,index_type_1,都指向别名index_type
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月19日
 * 
 */
@Service("indexMonitorService")
public class AliasMonitorService {

    /**
     * 
     */
    private static final Logger log = LoggerFactory.getLogger(AliasMonitorService.class);

    /**
     * 日志记录器
     */
    protected IndexUpdateLogger updateLogger = new IndexUpdateFileLogger();

    /**
     * 检查索引别名是否重复被指向不同的索引
     * <P>
     * 在重建索引时，删除索引出错也会导致这个情况
     */
    public void checkIndexNameAlias() {
        try {
            doService();
        } catch (Exception e) {
            log.error("checkIndexNameAlias is error! ", e);
        }
    }

    private void doService() {
        List<IndexType> indexs = IndexType.getAllIndex();
        IndicesAdminClient adminClient = ESClient.getClient().admin().indices();
        for (IndexType indexType : indexs) {
            String indexname = indexType.getIndexName().toLowerCase();
            String lastIndexName = null;
            try {
                lastIndexName = updateLogger.getLastIndexName(indexType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet()
                    .isExists()
                    && adminClient
                            .aliasesExist(
                                    new GetAliasesRequest().indices(indexType.index_type_1())
                                            .aliases(indexname)).actionGet().isExists()) {
                if (adminClient.prepareExists(indexType.index_type_2()).execute().actionGet()
                        .isExists()
                        && adminClient
                                .aliasesExist(
                                        new GetAliasesRequest().indices(indexType.index_type_2())
                                                .aliases(indexname)).actionGet().isExists()) {
                    log.error("indexname：{},{} alias:{}", indexType.index_type_1(),
                            indexType.index_type_2(), indexname);
                    if (lastIndexName != null && indexType.index_type_1().equals(lastIndexName)) {
                        adminClient.delete(new DeleteIndexRequest(indexType.index_type_2()));// 删除索引
                    } else {
                        adminClient.delete(new DeleteIndexRequest(indexType.index_type_1()));// 删除索引
                    }
                }
            }
        }
    }

}
