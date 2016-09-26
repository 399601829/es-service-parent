package com.es.service.index.core;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.client.ESClient;
import com.es.service.common.conf.Constants;
import com.es.service.common.type.IndexType;
import com.es.service.index.core.xmlparser.FieldXMLParser;
import com.es.service.index.core.xmlparser.Filed;
import com.es.service.index.core.xmlparser.Fileds;

/**
 * 
 * 处理编入索引相关的事务操作
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月16日
 * 
 */
public class IndexTransaction {

    /**
     */
    private static final Logger log = LoggerFactory.getLogger(IndexTransaction.class);

    /**
     * 批量执行的请求数量达到1000则强制提交一次
     */
    private static final int BULK_REQUEST_MAX_NUM = 1000;

    /**
     * 索引类型
     */
    private IndexType indexType;

    /**
     * 当前使用的索引名称
     */
    private String currentIndexName;

    // 全量标志
    private boolean needFull;

    // 事务id
    private String transactionId;

    // 批量请求
    private BulkRequestBuilder bulkRequest;

    // 请求体
    private XContentBuilder contentBuilder;

    /**
     * 构造Transaction
     * 
     * @param indexname
     * @param type
     * @param needFull
     * @param transactionId
     */
    private IndexTransaction(IndexType indexType, boolean needFull, String transactionId) {
        this.indexType = indexType;
        this.needFull = needFull;
        this.bulkRequest = ESClient.getClient().prepareBulk();
        if (null != transactionId) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            this.transactionId = String.format("%s_%s_%s", uuid, System.currentTimeMillis(),
                    this.currentIndexName);
        }

        // 设置事务索引
        if (needFull) {
            fullTransaction();
        } else {
            incrementTransaction();
        }
        // 构建mapping
        if (needFull && isMapping()) {
            buildMapping();
        }
    }

    /**
     * 创建事务
     * 
     * @param indexname
     * @param type
     * @param needFull
     * @param transactionId
     * @return
     */
    public static IndexTransaction createTransaction(IndexType indexType, boolean needFull) {
        try {
            // 创建事务
            IndexTransaction transaction = new IndexTransaction(indexType, needFull, null);

            ESClient.getClient().admin().cluster().prepareHealth(transaction.currentIndexName)
                    .setWaitForGreenStatus().setTimeout("10s").execute().actionGet();
            return transaction;
        } catch (Exception e) {
            log.error("createTransaction error !", e);
            ESClient.closeClient();
            return null;
        }
    }

    /**
     * 设置全量索引事务
     * <p>
     * 第一个if 为索引完全不存在，设置为全量索引
     * <p>
     * 第二个if 判断后缀为1的索引存在且否具备别名。即正在使用
     * <p>
     * 第三个if 判断后缀为2 的索引是否存在并具备别名，即正在使用
     * <p>
     * 其他，删除后缀为1的索引，并重建
     * 
     * @param transaction
     */
    private void fullTransaction() {
        // 分片数
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("number_of_shards", Constants.index_number_of_shards).build();
        IndicesAdminClient adminClient = ESClient.getClient().admin().indices();

        // 索引不存在,直接新创建索引
        if (!adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()
                && !adminClient.prepareExists(indexType.index_type_2()).execute().actionGet()
                        .isExists()) {
            try {
                ESClient.getClient().admin().indices().prepareCreate(indexType.index_type_1())
                        .setSettings(settings).execute().actionGet();
                currentIndexName = indexType.index_type_1();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // 存在索引indexname_1且indexname的别名指向它，同时indexname_2索引存在，则删除indexname_2然后重建indexname_2
        if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()
                && adminClient
                        .aliasesExist(
                                new GetAliasesRequest().indices(indexType.index_type_1()).aliases(
                                        indexType.getIndexName())).actionGet().isExists()) {
            if (adminClient.prepareExists(indexType.index_type_2()).execute().actionGet()
                    .isExists()) {
                adminClient.delete(new DeleteIndexRequest(indexType.index_type_2()));
            }
            adminClient.prepareCreate(indexType.index_type_2()).setSettings(settings).execute()
                    .actionGet();
            currentIndexName = indexType.index_type_2();
            return;
        }
        // 存在索引 indexname_2且indexname的别名指向它，同时indexname_1索引存在，则删除indexname_1然后重建indexname_1
        if (adminClient.prepareExists(indexType.index_type_2()).execute().actionGet().isExists()
                && adminClient
                        .aliasesExist(
                                new GetAliasesRequest().indices(indexType.index_type_2()).aliases(
                                        indexType.getIndexName())).actionGet().isExists()) {
            if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet()
                    .isExists()) {
                adminClient.delete(new DeleteIndexRequest(indexType.index_type_1()));
            }
            adminClient.prepareCreate(indexType.index_type_1()).setSettings(settings).execute()
                    .actionGet();
            currentIndexName = indexType.index_type_1();
            return;
        }

        // 创建indexname_1索引，存在索引 indexname_1则删除然后重建
        if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()) {
            adminClient.delete(new DeleteIndexRequest(indexType.index_type_1()));
        }
        adminClient.prepareCreate(indexType.index_type_1()).setSettings(settings).execute()
                .actionGet();
        currentIndexName = indexType.index_type_1();
    }

    /**
     * 设置增量索引
     * <p>
     * 第一个if ,为索引完全不存在，设置为全量索引
     * <p>
     * 第二个if ,判断后缀为1的索引存在且否具备别名。即正在使用
     * <p>
     * 第三个if,判断后缀为2 的索引是否存在并具备别名，即正在使用
     * <p>
     * 其他,重置状态为全量
     * 
     * @param transaction
     */
    private void incrementTransaction() {

        // 分片数
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("number_of_shards", Constants.index_number_of_shards).build();
        IndicesAdminClient adminClient = ESClient.getClient().admin().indices();

        // 索引不存在,直接新创建索引
        if (!adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()
                && !adminClient.prepareExists(indexType.index_type_2()).execute().actionGet()
                        .isExists()) {
            adminClient.prepareCreate(indexType.index_type_1()).setSettings(settings).execute()
                    .actionGet();
            currentIndexName = indexType.index_type_1();
            needFull = true;
            return;
        }
        // 存在索引 indexname_1且indexname的别名指向它，则设置有效索引为indexname_1
        if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()
                && adminClient
                        .aliasesExist(
                                new GetAliasesRequest().indices(indexType.index_type_1()).aliases(
                                        indexType.getIndexName())).actionGet().isExists()) {
            currentIndexName = indexType.index_type_1();
            needFull = false;
            return;
        }

        // 存在索引 indexname_2且indexname的别名指向它，则设置有效索引为indexname_2
        if (adminClient.prepareExists(indexType.index_type_2()).execute().actionGet().isExists()
                && adminClient
                        .aliasesExist(
                                new GetAliasesRequest().indices(indexType.index_type_2()).aliases(
                                        indexType.getIndexName())).actionGet().isExists()) {
            currentIndexName = indexType.index_type_2();
            needFull = false;
            return;
        }

        // 存在索引indexname_1和indexname_2，重建indexname_1,currentIndexName指向indexname_1
        if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()
                && adminClient.prepareExists(indexType.index_type_2()).execute().actionGet()
                        .isExists()) {
            adminClient.delete(new DeleteIndexRequest(indexType.index_type_1()));
            adminClient.prepareCreate(indexType.index_type_1()).setSettings(settings).execute()
                    .actionGet();
            currentIndexName = indexType.index_type_1();
            needFull = true;
            return;
        }

        // 存在索引 indexname_1,且不存在indexname_2，重建index_name_2,currentIndexName指向indexname_2
        if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet().isExists()) {
            adminClient.prepareCreate(indexType.index_type_2()).setSettings(settings).execute()
                    .actionGet();
            currentIndexName = indexType.index_type_2();
            needFull = true;
            return;
        } else {
            // 创建indexname_1,currentIndexName指向indexname_1
            adminClient.prepareCreate(indexType.index_type_1()).setSettings(settings).execute()
                    .actionGet();
            currentIndexName = indexType.index_type_1();
            needFull = true;
        }
    }

    /**
     * 索引提交
     * 
     * @return
     */
    public int commitIndex() {
        // 未提交的action>0
        if (this.bulkRequest.numberOfActions() > 0) {
            BulkResponse actionGet = this.bulkRequest.execute().actionGet();
            if (actionGet.hasFailures()) {
                // 失败
                log.error(actionGet.buildFailureMessage());
            }
        }
        ESClient.getClient().admin().indices().flush(new FlushRequest(this.currentIndexName))
                .actionGet();
        if (needFull) {
            // 基本配置-副本
            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("number_of_replicas", Constants.index_number_of_replicas).build();
            // 首先更新索引库配置
            ESClient.getClient().admin().indices().prepareUpdateSettings(this.currentIndexName)
                    .setSettings(settings).execute().actionGet();
            // 需要删除的索引
            String delName = this.currentIndexName.equals(indexType.index_type_1()) ? indexType
                    .index_type_2() : indexType.index_type_1();
            try {
                // 删除的索引存在
                if (ESClient.getClient().admin().indices().prepareExists(delName).execute()
                        .actionGet().isExists()) {
                    // 即将移除的索引是正在使用的所有
                    if (ESClient
                            .getClient()
                            .admin()
                            .indices()
                            .aliasesExist(
                                    new GetAliasesRequest().indices(delName).aliases(
                                            indexType.getIndexName())).actionGet().isExists()) {
                        // 移除正在使用索引和即将删除的索引之间的关系
                        ESClient.getClient()
                                .admin()
                                .indices()
                                .aliases(
                                        new IndicesAliasesRequest().removeAlias(delName,
                                                indexType.getIndexName())).actionGet();
                        // 建立刚创建的索引和使用的索引之间的关系
                        ESClient.getClient()
                                .admin()
                                .indices()
                                .aliases(
                                        new IndicesAliasesRequest().addAlias(
                                                indexType.getIndexName(), currentIndexName))
                                .actionGet();
                        // 删除索引
                        ESClient.getClient().admin().indices()
                                .delete(new DeleteIndexRequest(delName));
                    } else {
                        // 建立刚创建的索引和使用的索引之间的关系
                        ESClient.getClient()
                                .admin()
                                .indices()
                                .aliases(
                                        new IndicesAliasesRequest().addAlias(
                                                indexType.getIndexName(), currentIndexName))
                                .actionGet();
                        // 删除索引
                        ESClient.getClient().admin().indices()
                                .delete(new DeleteIndexRequest(delName));
                    }
                } else {
                    // 建立刚创建的索引和使用的索引之间的关系
                    ESClient.getClient()
                            .admin()
                            .indices()
                            .aliases(
                                    new IndicesAliasesRequest().addAlias(indexType.getIndexName(),
                                            currentIndexName)).actionGet();
                }

                ESClient.getClient().admin().indices()
                        .flush(new FlushRequest(this.currentIndexName)).actionGet();
            } catch (Exception e) {
                log.error("commitIndex error ! cause is create index and aliase connect failure.",
                        e);
                throw new RuntimeException(e);
            }
            return 0;
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        return 0;
    }

    /**
     * 编入索引
     * 
     * @param jsonObject
     * @return
     */
    public int indexed(String jsonObject) {
        if (Constants.isDebug) {
            log.debug(jsonObject);
        }
        if (null == this.bulkRequest) {
            this.bulkRequest = ESClient.getClient().prepareBulk();
        } else {
            // 批量提交
            if (this.bulkRequest.numberOfActions() >= BULK_REQUEST_MAX_NUM) {
                long start = System.currentTimeMillis();
                BulkResponse actionGet = this.bulkRequest.execute().actionGet();
                if (actionGet.hasFailures()) {
                    // 失败
                    log.error(actionGet.buildFailureMessage());
                }
                long end = System.currentTimeMillis();
                log.warn("bulkRequest commit {} indexed,time in {}", this.currentIndexName,
                        (end - start));
                this.bulkRequest = ESClient.getClient().prepareBulk();
                // 增量可保持实时搜索
                ESClient.getClient().admin().indices()
                        .refresh(new RefreshRequest(this.currentIndexName)).actionGet();
                // ESClient.getClient().admin().indices().flush(new FlushRequest(this.currentIndexName)).actionGet();
            }
        }
        this.bulkRequest
                .add(ESClient.getClient()
                        .prepareIndex(this.currentIndexName, indexType.getTypeName())
                        .setSource(jsonObject));
        return 0;
    }

    /**
     * 设置mapping
     * 
     * @return
     */
    private void buildMapping() {
        try {
            Fileds fileds = FieldXMLParser.getAndCache().get(indexType.getIndexNo());
            contentBuilder = XContentFactory.jsonBuilder();
            contentBuilder.startObject().startObject(indexType.getTypeName());
            // 如果有主键,用主键作为新建索引id,没有主键让es自动生成主键
            if (StringUtils.isNotBlank(fileds.getKey())) {
                String idname = fileds.getKey().toUpperCase();
                contentBuilder.startObject("_id").field("path", idname).endObject();
            }
            // 压缩
            contentBuilder.startObject("_source").field("compress", true).endObject();
            // 开启_all
            contentBuilder.startObject("_all").field("enabled", true).endObject();
            contentBuilder.startObject("properties");

            List<Filed> listfiled = fileds.getListfiled();
            for (Filed filed : listfiled) {
                // 处理类型映射
                String type_ = getFiledType(filed);
                if ("-1".equals(type_)) {
                    continue;
                } else if ("date".equals(type_)) {
                    String format = filed.getFormat();
                    if (StringUtils.isBlank(format)) {
                        format = "yyyy-MM-dd HH:mm:ss";
                    }
                    contentBuilder.startObject(filed.getNameToUpperCase()).field("type", type_)
                            .field("format", format)
                            .field("store", filed.isIsstore() == true ? "yes" : "no")
                            .field("index", "not_analyzed")
                            .field("include_in_all", filed.isIsdefaultsearch()).endObject();
                    continue;
                }

                // mapping基本配置
                contentBuilder.startObject(filed.getNameToUpperCase()).field("type", type_)
                        .field("store", filed.isIsstore() == true ? "yes" : "no")
                        .field("include_in_all", filed.isIsdefaultsearch()); //
                // 是否copyto
                if (filed.isIscopy()) {
                    contentBuilder.field("copy_to", filed.getCopyto());
                }
                // 是否设置有权重
                if (filed.getWeight() > 0d) {
                    contentBuilder.field("boost", filed.getWeight());
                }
                // 多值字段的边界分割
                if (filed.getPosition_offset_gap() > 0) {
                    contentBuilder.field("position_offset_gap", filed.getPosition_offset_gap());
                }

                // 设置索引分词方式
                if (!filed.isIsindex()) {
                    contentBuilder.field("index", "no");
                } else {
                    if (!filed.isAnalyzer()) {
                        contentBuilder.field("index", "not_analyzed");
                    } else {
                        String indexAnalyzer = filed.getIndexAnalyzer();
                        String searchAnalyzer = filed.getSearchAnalyzer();
                        if (StringUtils.isBlank(searchAnalyzer)) {
                            searchAnalyzer = indexAnalyzer;
                        }
                        contentBuilder.field("indexAnalyzer", indexAnalyzer).field(
                                "searchAnalyzer", searchAnalyzer);
                    }
                }
                contentBuilder.endObject();
            }

            // 建议器
            if (fileds.getSuggest() != null) {
                contentBuilder.startObject(fileds.getSuggest().key)
                        .field("type", fileds.getSuggest().type)
                        .field("index_analyzer", fileds.getSuggest().getIndexAnalyzer())
                        .field("search_analyzer", fileds.getSuggest().getSearchAnalyzer())
                        .field("payloads", fileds.getSuggest().isPayloads()).endObject();
            }

            // 构造mapping请求
            PutMappingRequest mappingRequest = Requests.putMappingRequest(currentIndexName)
                    .type(indexType.getTypeName()).source(contentBuilder.endObject().endObject());
            ESClient.getClient().admin().indices().putMapping(mappingRequest).actionGet();
            ESClient.getClient().admin().indices().flush(new FlushRequest(currentIndexName))
                    .actionGet();
            log.debug("create mappings. index:{},type:{},mapping:{}", currentIndexName,
                    indexType.getTypeName(), contentBuilder.string());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文档
     * 
     * @param id
     */
    public boolean deleteDocById(String docId) {
        try {
            // 删除
            DeleteResponse resp = ESClient.getClient()
                    .prepareDelete(this.currentIndexName, this.indexType.getTypeName(), docId)
                    .setOperationThreaded(false).execute().actionGet();
            // 刷新
            ESClient.getClient().admin().indices()
                    .refresh(new RefreshRequest(this.currentIndexName)).actionGet();
            if (resp.isFound()) {
                log.warn("delete index sunccess,indexname:{},type:{},delete {} items",
                        this.indexType.getIndexName(), this.indexType.getTypeName(), 1);
                return resp.isFound();
            }
        } catch (Exception e) {
            log.error("delete Doc fail,indexname:{},type:{}", this.indexType.getIndexName(),
                    this.indexType.getTypeName());
        }
        return false;
    }

    /**
     * 删除索引
     * 
     * @param index_type
     */
    public boolean deleteIndex(String index_type) {
        try {
            AdminClient adminClient = ESClient.getClient().admin();
            if (adminClient.indices().prepareExists(index_type).execute().actionGet().isExists()) {
                ESClient.getClient().admin().indices().delete(new DeleteIndexRequest(index_type));
            }
            if (adminClient.indices().prepareExists(index_type + "_1").execute().actionGet()
                    .isExists()) {
                ESClient.getClient().admin().indices()
                        .delete(new DeleteIndexRequest(index_type + "_1"));
            }
            if (adminClient.indices().prepareExists(index_type + "_2").execute().actionGet()
                    .isExists()) {
                ESClient.getClient().admin().indices()
                        .delete(new DeleteIndexRequest(index_type + "_2"));
            }
            return true;
        } catch (Exception e) {
            log.error("delete index fail,indexname:{}", index_type);
        }
        return false;
    }

    /**
     * 返回es各数据，如找不到对应则返回-1
     * 
     * @param filed
     * @return
     */
    private static String getFiledType(Filed filed) {
        String type_ = "string";
        switch (filed.getType()) {
        case "int":
            type_ = "integer";
            break;
        case "long":
            type_ = "long";
            break;
        case "string":
            type_ = "string";
            break;
        case "float":
            type_ = "float";
            break;
        case "double":
            type_ = "double";
            break;
        case "boolean":
            type_ = "boolean";
            break;
        case "date":
            type_ = "date";
            break;
        default:
            type_ = "-1";
            break;
        }
        return type_;
    }

    /**
     * @return the indexType
     */
    public IndexType getIndexType() {
        return indexType;
    }

    /**
     * @param indexType the indexType to set
     */
    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    /**
     * @return the currentIndexName
     */
    public String getCurrentIndexName() {
        return currentIndexName;
    }

    /**
     * @param currentIndexName the currentIndexName to set
     */
    public void setCurrentIndexName(String currentIndexName) {
        this.currentIndexName = currentIndexName;
    }

    /**
     * @return the needFull
     */
    public boolean isNeedFull() {
        return needFull;
    }

    /**
     * @param needFull the needFull to set
     */
    public void setNeedFull(boolean needFull) {
        this.needFull = needFull;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the bulkRequest
     */
    public BulkRequestBuilder getBulkRequest() {
        return bulkRequest;
    }

    /**
     * @param bulkRequest the bulkRequest to set
     */
    public void setBulkRequest(BulkRequestBuilder bulkRequest) {
        this.bulkRequest = bulkRequest;
    }

    /**
     * @return the contentBuilder
     */
    public XContentBuilder getContentBuilder() {
        return contentBuilder;
    }

    /**
     * @param contentBuilder the contentBuilder to set
     */
    public void setContentBuilder(XContentBuilder contentBuilder) {
        this.contentBuilder = contentBuilder;
    }

    public boolean isMapping() {
        return this.contentBuilder == null;
    }

}
