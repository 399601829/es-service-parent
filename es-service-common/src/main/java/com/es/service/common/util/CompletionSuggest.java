package com.es.service.common.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 自动补全建议器
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月18日
 * 
 */
public class CompletionSuggest implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1149700421868480742L;

    public static final String key = "SUGGEST";

    public static final String type = "completion";

    /**
     * 索引字段分词器
     */
    private String indexAnalyzer = "edge_ngram";

    /**
     * 搜索分词器-默认为逐字延长分词
     */
    private String searchAnalyzer = "edge_ngram";

    /**
     * 是否返回附加信息-默认为逐字延长分词
     */
    private boolean payloads = true;

    /**
     * 
     * 类/接口注释
     * 
     * @author hailin0@yeah.net
     * @createDate 2016年9月18日
     * 
     */
    public static class SuggestBuilder {

        private Set<String> input;

        private String output;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Object> payload;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        private int weight;

        /**
         * @param output
         * @param payload
         */
        public SuggestBuilder(String output, Map<String, Object> payload) {
            super();
            this.output = output;
            this.payload = payload;

            this.input = new HashSet<String>();
            this.input.add(output);
            this.input.add(PinYinHelper.getInstance().getPinYin(output));
            this.input.add(PinYinHelper.getInstance().getPinYinPrefix(output));
        }

        /**
         * 扩展-通过此方法可添加额外的补全词
         * 
         * @param word
         * @return
         */
        public SuggestBuilder addInput(String... word) {
            input.addAll(Arrays.asList(word));
            return this;
        }

        /**
         * @return the input
         */
        public Set<String> getInput() {
            return input;
        }

        /**
         * @param input the input to set
         */
        public void setInput(Set<String> input) {
            this.input = input;
        }

        /**
         * @return the output
         */
        public String getOutput() {
            return output;
        }

        /**
         * @param output the output to set
         */
        public void setOutput(String output) {
            this.output = output;
        }

        /**
         * @return the payload
         */
        public Map<String, Object> getPayload() {
            return payload;
        }

        /**
         * @param payload the payload to set
         */
        public void setPayload(Map<String, Object> payload) {
            this.payload = payload;
        }

        /**
         * @return the weight
         */
        public int getWeight() {
            return weight;
        }

        /**
         * @param weight the weight to set
         */
        public void setWeight(int weight) {
            this.weight = weight;
        }

    }

    /**
     * 
     * 查询体
     * 
     * @author hailin0@yeah.net
     * @createDate 2016年9月18日
     * 
     */
    public static class SuggestQuery implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 4387816230333713773L;

        private static final String suggestName = CompletionSuggest.type;

        private static final String type = CompletionSuggest.type;

        private String text;

        private String field = key;

        private int size = 10;

        private String sort = "score";

        /**
         * 模糊字符参数
         */
        private int fuzziness = 0;

        /**
         * 
         */
        public SuggestQuery() {
        }

        /**
         * @param text
         */
        public SuggestQuery(String text) {
            this.text = text;
        }

        /**
         * @return the field
         */
        public String getField() {
            return field;
        }

        /**
         * @param field the field to set
         */
        public void setField(String field) {
            this.field = field;
        }

        /**
         * @return the size
         */
        public int getSize() {
            return size;
        }

        /**
         * @param size the size to set
         */
        public void setSize(int size) {
            this.size = size;
        }

        /**
         * @return the sort
         */
        public String getSort() {
            return sort;
        }

        /**
         * @param sort the sort to set
         */
        public void setSort(String sort) {
            this.sort = sort;
        }

        /**
         * @return the fuzziness
         */
        public int getFuzziness() {
            return fuzziness;
        }

        /**
         * @param fuzziness the fuzziness to set
         */
        public void setFuzziness(int fuzziness) {
            this.fuzziness = fuzziness;
        }

        /**
         * @return the suggestname
         */
        public static String getSuggestname() {
            return suggestName;
        }

        /**
         * @return the type
         */
        public static String getType() {
            return type;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * @param text the text to set
         */
        public void setText(String text) {
            this.text = text;
        }

    }

    /**
     * @return the indexAnalyzer
     */
    public String getIndexAnalyzer() {
        return indexAnalyzer;
    }

    /**
     * @param indexAnalyzer the indexAnalyzer to set
     */
    public void setIndexAnalyzer(String indexAnalyzer) {
        this.indexAnalyzer = indexAnalyzer;
    }

    /**
     * @return the searchAnalyzer
     */
    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    /**
     * @param searchAnalyzer the searchAnalyzer to set
     */
    public void setSearchAnalyzer(String searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }

    /**
     * @return the payloads
     */
    public boolean isPayloads() {
        return payloads;
    }

    /**
     * @param payloads the payloads to set
     */
    public void setPayloads(boolean payloads) {
        this.payloads = payloads;
    }

}
