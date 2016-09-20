package com.es.service.common.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

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

        @JsonIgnore
        public static final String key = CompletionSuggest.key;

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
            addInput(output);
            addInput(PinYinHelper.getPinYin(output));
            addInput(PinYinHelper.getPinYinPrefix(output));
        }

        /**
         * 扩展-通过此方法可添加额外的补全词
         * 
         * @param word
         * @return
         */
        public SuggestBuilder addInput(String word) {
            input.add(word.contains(" ")? word.replaceAll(" ", ""):word);
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

        public static final String suggestName = CompletionSuggest.type;

        public static final String type = CompletionSuggest.type;

        private String text;

        private String field = key;

        private int size = 10;

        private String sort = "score";

        /**
         * 模糊字符参数 0、1、2、AUTO
         */
        private Object fuzziness = "AUTO";

        /**
         * 
         */
        public SuggestQuery() {
        }

        /**
         * @param text
         */
        public SuggestQuery(String text) {
            this.text = text.contains(" ")? text.replaceAll(" ", ""):text;
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
        public SuggestQuery setSize(int size) {
            this.size = size;
            return this;
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
        public Object getFuzziness() {
            if ("AUTO".equals(fuzziness.toString())) {
                int len = text == null ? 5 : text.codePointCount(0, text.length());
                if (len <= 2) {
                    return 0;
                } else if (len > 5) {
                    return 2;
                } else {
                    return 1;
                }
            }
            return fuzziness;
        }

        /**
         * @param fuzziness the fuzziness to set
         */
        public SuggestQuery setFuzziness(int fuzziness) {
            if (fuzziness < 0 || fuzziness > 2) {
                throw new RuntimeException("fuzziness 范围在0-2为有效！");
            }
            this.fuzziness = fuzziness;
            return this;
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
        public SuggestQuery setText(String text) {
            this.text = text.contains(" ")? text.replaceAll(" ", ""):text;
            return this;
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
