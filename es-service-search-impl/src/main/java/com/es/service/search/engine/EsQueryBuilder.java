package com.es.service.search.engine;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.ScriptFilterBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.script.ScriptScoreFunctionBuilder;

import com.es.service.common.client.ESClient;
import com.es.service.common.conf.Constants;
import com.es.service.common.type.IndexType;
import com.es.service.search.to.Condition;
import com.es.service.search.to.EsFilterScript;
import com.es.service.search.to.EsRequest;
import com.es.service.search.to.ScoreScript;
import com.es.service.search.to.SearchCondition;
import com.es.service.search.type.ConditionType;
import com.es.service.search.util.KeyWordUtil;
import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;

/**
 * 构建查询体
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月31日
 * 
 */
public class EsQueryBuilder {

    /**
     * 索引名
     */
    private String indexName;

    /**
     * Request
     */
    private EsRequest request;

    /**
     * 此参数指定query_string默认的逻辑运算符（or或and），默认值是or
     */
    private QueryStringQueryBuilder.Operator default_operator = Operator.OR;

    public EsQueryBuilder(EsRequest request) {
        this.indexName = request.getIndexname();
        this.request = request;
        String operator = request.getSafeExtend().get("DEFAULT_OPERATOR");
        if (StringUtils.isNotBlank(operator)) {
            default_operator = "and".equals(operator.toLowerCase()) ? Operator.AND : Operator.OR;
        }
    }

    /**
     * 构建查询
     * 
     * @return
     */
    public QueryBuilder makeQueryBuilder(SearchRequestBuilder srb) {

        // 拼接查询层次
        BoolQueryBuilder boolQ = QueryBuilders.boolQuery();
        Map<SearchCondition, ConditionType> searchTypeMap = this.request.getSearchConditions();
        ConditionType conditionType;
        BoolQueryBuilder subBoolQ;
        for (SearchCondition searchCondition : searchTypeMap.keySet()) {
            conditionType = searchTypeMap.get(searchCondition);
            subBoolQ = queryBuilder(searchCondition, conditionType);
            mergeBuilder(boolQ, subBoolQ, conditionType);
        }
        // 没有条件直接返回
        if (!boolQ.hasClauses()) {
            return null;
        }
        // 得分脚本
        if (request.getSafeScoreScript().size() < 1) {
            srb.setQuery(boolQ);
            return boolQ;
        }
        String boost_mode = request.getSafeExtend().get(Constants.BOOST_MODE_KEY);
        if (StringUtils.isBlank(boost_mode)) {
            boost_mode = Constants.boost_mode;
        }
        FunctionScoreQueryBuilder functionScoreQ = QueryBuilders.functionScoreQuery(boolQ);
        functionScoreQ.boostMode(boost_mode);
        for (ScoreScript scoreScript : request.getSafeScoreScript()) {
            ScriptScoreFunctionBuilder builder = ScoreFunctionBuilders.scriptFunction(scoreScript
                    .getScript());
            if (scoreScript.getScriptParams() != null && scoreScript.getScriptParams().size() > 0) {
                builder.params(scoreScript.getScriptParams());
            }
            functionScoreQ.add(builder);
        }
        srb.setQuery(functionScoreQ);
        return functionScoreQ;
    }

    /**
     * 获取过滤器
     * 
     * @return
     */
    public BoolFilterBuilder makeFilterBuilder(SearchRequestBuilder srb) {
        // 过滤器
        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();
        if (request.getSafeFilterScript().size() > 0) {
            ScriptFilterBuilder filter;
            for (EsFilterScript filterScript : request.getSafeFilterScript()) {
                filter = FilterBuilders.scriptFilter(filterScript.getScript());
                if (filterScript.getScriptParams() != null
                        && filterScript.getScriptParams().size() > 0) {
                    filter.params(filterScript.getScriptParams());
                }
                boolFilterBuilder.should(filter);
            }
            srb.setPostFilter(boolFilterBuilder);
        }
        return boolFilterBuilder;
    }

    /**
     * 构造查询以及处理子查询
     * 
     * 
     * @param searchCondition
     * @param conditionType
     * @return
     */
    private BoolQueryBuilder queryBuilder(SearchCondition searchCondition,
            ConditionType conditionType) {
        BoolQueryBuilder boolQ = QueryBuilders.boolQuery();
        // 子查询
        if (searchCondition.hasSubquery()) {
            // 构建子查询结构逻辑
            List<SearchCondition> subquery = searchCondition.getSubquery();
            ConditionType subQueryType = searchCondition.getSubQueryType();
            BoolQueryBuilder subBoolQ;
            for (SearchCondition subCondition : subquery) {
                subBoolQ = queryBuilder(subCondition, subCondition.getConditionType());
                mergeBuilder(boolQ, subBoolQ, subQueryType);
            }
        }

        // 条件不为空
        if (!searchCondition.getConditions().isEmpty()) {
            // 分词验证
            boolean request_analyzetoken = Constants.request_analyzeToken;
            String request_analyzetoken_key = request.getSafeExtend().get(
                    Constants.REQUEST_ANALYZETOKEN_KEY);
            if (StringUtils.isNotBlank(request_analyzetoken_key)) {
                request_analyzetoken = Boolean.parseBoolean(request_analyzetoken_key);
            }
            request_analyzetoken = request_analyzetoken ? requestAnalyzeToken(searchCondition
                    .getConditions()) : false;

            // 构建搜索逻辑
            switch (searchCondition.getSearchType()) {
            case TERM:
                doTerm(boolQ, searchCondition.getConditions(), searchCondition.getConditionType());
                break;
            case MATCHING_PHRASE:
                doMatchPhrase(boolQ, searchCondition.getConditions(),
                        searchCondition.getConditionType());
                break;
            case PREFIX:
                doPrefix(boolQ, searchCondition.getConditions(), searchCondition.getConditionType());
                break;
            case FUZZ:
                doFuzz(boolQ, searchCondition.getConditions(), searchCondition.getConditionType());
                break;
            case QUERY_STRING:
                doQueryString(boolQ, searchCondition.getConditions(),
                        searchCondition.getConditionType());
                break;
            case RANGE:
                doRange(boolQ, searchCondition.getConditions(), searchCondition.getConditionType());
                break;
            default:
                break;
            }

        }
        return boolQ;
    }

    /**
     * 精确匹配处理
     * 
     * @param boolQ
     * @param conditions
     * @param conditionType
     */
    private void doTerm(BoolQueryBuilder boolQ, List<Condition> conditions,
            ConditionType conditionType) {
        QueryBuilder term;
        BoolQueryBuilder subBoolQ;
        for (Condition condition : conditions) {
            if (condition.isSpaceSplit()) {
                subBoolQ = QueryBuilders.boolQuery();
                for (String word : KeyWordUtil.processKeyWord(condition.getValue())) {
                    subBoolQ.should(QueryBuilders.termQuery(condition.getFiled(), word));
                }
                term = subBoolQ;
            } else {
                term = QueryBuilders.termQuery(condition.getFiled(), condition.getValue());
            }
            mergeBuilder(boolQ, term, conditionType);
        }
    }

    /**
     * 短语查询-支持带空格
     * 
     * @param boolQ
     * @param conditions
     * @param conditionType
     */
    private void doMatchPhrase(BoolQueryBuilder boolQ, List<Condition> conditions,
            ConditionType conditionType) {
        String filed;
        QueryBuilder match;
        BoolQueryBuilder subBoolQ;
        for (Condition condition : conditions) {
            // 字段名为空则在_all上搜索
            filed = condition.getFiled();
            filed = StringUtils.isBlank(filed) ? "_all" : filed;
            if (condition.isSpaceSplit()) {
                subBoolQ = QueryBuilders.boolQuery();
                for (String word : KeyWordUtil.processKeyWord(condition.getValue())) {
                    subBoolQ.should(QueryBuilders.matchPhraseQuery(filed, word));
                }
                match = subBoolQ;
            } else {
                match = QueryBuilders.matchPhraseQuery(filed, condition.getValue());
            }
            mergeBuilder(boolQ, match, conditionType);
        }
    }

    /**
     * 前缀查询
     * 
     * @param boolQ
     * @param conditions
     * @param conditionType
     */
    private void doPrefix(BoolQueryBuilder boolQ, List<Condition> conditions,
            ConditionType conditionType) {
        QueryBuilder prefix;
        BoolQueryBuilder subBoolQ;
        for (Condition condition : conditions) {
            if (condition.isSpaceSplit()) {
                subBoolQ = QueryBuilders.boolQuery();
                for (String word : KeyWordUtil.processKeyWord(condition.getValue())) {
                    subBoolQ.should(QueryBuilders.prefixQuery(condition.getFiled(), word));
                }
                prefix = subBoolQ;
            } else {
                prefix = QueryBuilders.prefixQuery(condition.getFiled(), condition.getValue());
            }
            mergeBuilder(boolQ, prefix, conditionType);
        }
    }

    /**
     * 模糊查询-支持带空格
     * 
     * @param boolQ
     * @param conditions
     * @param conditionType
     */
    private void doFuzz(BoolQueryBuilder boolQ, List<Condition> conditions,
            ConditionType conditionType) {
        BoolQueryBuilder builder;
        String filed;
        for (Condition condition : conditions) {
            builder = QueryBuilders.boolQuery();
            filed = condition.getFiled();
            // 字段名为空则在_all上搜索
            filed = StringUtils.isBlank(filed) ? "_all" : filed;
            if (condition.isSpaceSplit()) {
                for (String word : KeyWordUtil.processKeyWord(condition.getValue())) {
                    // fuzziness 0.0到1.0,设置相似性.AUTO为自动
                    builder.should(QueryBuilders.matchPhrasePrefixQuery(filed, word)
                            .fuzziness(org.elasticsearch.common.unit.Fuzziness.AUTO)
                            .prefixLength(1));
                    // slop 1
                    builder.should(QueryBuilders.matchPhrasePrefixQuery(filed, word).slop(1));
                    // prefix
                    builder.should(QueryBuilders.prefixQuery(filed, word));
                }
            } else {
                // fuzziness 0.0到1.0,设置相似性.AUTO为自动
                builder.should(QueryBuilders.matchPhrasePrefixQuery(filed, condition.getValue())
                        .fuzziness(org.elasticsearch.common.unit.Fuzziness.AUTO).prefixLength(1));
                // slop 1
                builder.should(QueryBuilders.matchPhrasePrefixQuery(filed, condition.getValue())
                        .slop(1));
                // prefix
                builder.should(QueryBuilders.prefixQuery(filed, condition.getValue()));
            }

            // 合并
            mergeBuilder(boolQ, builder, conditionType);
        }
    }

    /**
     * 范围查询
     * 
     * @param boolQ
     * @param conditions
     * @param conditionType
     */
    private void doRange(BoolQueryBuilder boolQ, List<Condition> conditions,
            ConditionType conditionType) {
        String[] ranges;
        RangeQueryBuilder range;
        for (Condition condition : conditions) {
            ranges = EsRange.rangeAdapter(condition.getValue());
            range = QueryBuilders.rangeQuery(condition.getFiled()).from(ranges[0]).to(ranges[1]);
            mergeBuilder(boolQ, range, conditionType);
        }
    }

    /**
     * query_string-分词搜索
     * 
     * @param boolQ
     * @param conditions
     * @param conditionType
     */
    private void doQueryString(BoolQueryBuilder boolQ, List<Condition> conditions,
            ConditionType conditionType) {
        // 构造查询语法串
        StringBuilder build = new StringBuilder();
        String filed;
        List<String> words;
        for (Condition condition : conditions) {
            filed = condition.getFiled();
            filed = StringUtils.isBlank(filed) ? "_all" : filed;
            words = KeyWordUtil.processKeyWord(condition.getValue());
            for (String word : words) {
                build.append("(").append(filed).append(":").append(word).append(") ")
                        .append(conditionType.name());
            }
        }
        if (build.length() < 1) {
            return;
        }
        String querystring = build.substring(0, build.lastIndexOf(conditionType.name()));
        QueryStringQueryBuilder queryString = QueryBuilders.queryString(querystring)
                .defaultOperator(default_operator);
        mergeBuilder(boolQ, queryString, conditionType);
    }

    /**
     * 合并查询
     * 
     * @param boolQ
     * @param subQ
     * @param conditionType
     */
    private BoolQueryBuilder mergeBuilder(BoolQueryBuilder boolQ, QueryBuilder subQ,
            ConditionType conditionType) {
        // 如果子查询为空
        if (subQ instanceof BoolQueryBuilder && !((BoolQueryBuilder) subQ).hasClauses()) {
            return boolQ;
        }
        if (conditionType.equals(conditionType.AND)) {
            boolQ.must(subQ);
        } else if (conditionType.equals(conditionType.OR)) {
            boolQ.should(subQ);
        }
        return boolQ;
    }

    /**
     * 检测分词，将无法分词的删除
     * 
     * @param conditions
     * @return
     */
    private boolean requestAnalyzeToken(List<Condition> conditions) {
        if (conditions != null) {
            IndexType indexType = IndexType.getIndexType(indexName);
            String realIndex = "";
            IndicesAdminClient adminClient = ESClient.getClient().admin().indices();
            if (adminClient.prepareExists(indexType.index_type_1()).execute().actionGet()
                    .isExists()) {
                realIndex = indexType.index_type_1();
            }
            if (adminClient.prepareExists(indexType.index_type_2()).execute().actionGet()
                    .isExists()) {
                realIndex = indexType.index_type_2();
            }
            List<Condition> copys = Lists.newArrayList(conditions);
            for (Iterator<Condition> iterator = copys.iterator(); iterator.hasNext();) {
                Condition condition = (Condition) iterator.next();
                if (condition != null) {
                    AnalyzeResponse ar = ESClient
                            .getClient()
                            .admin()
                            .indices()
                            .analyze(
                                    new AnalyzeRequest(condition.getValue()).analyzer("ik").index(
                                            realIndex)).actionGet();
                    if (ar == null || ar.getTokens() == null || ar.getTokens().size() < 1) {
                        conditions.remove(condition);
                    }
                }
            }
            if (conditions.size() < 1) {
                return false;
            }
        }
        return true;
    }

}
