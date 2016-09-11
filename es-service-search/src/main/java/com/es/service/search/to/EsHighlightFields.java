package com.es.service.search.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 高亮
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月10日
 *
 */
public class EsHighlightFields implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2043109610882947113L;

    private String preTags = "<FONT COLOR='RED'>";

    private String postTags = "</FONT>";

    private int fragmentSize = 20;

    private int numOfFragments = 3;

    private boolean requireFieldMatch = true;

    private List<String> fields = new ArrayList<String>();

    /**
     * 
     */
    public EsHighlightFields() {
        super();
    }

    /**
     * @param preTags
     * @param postTags
     */
    public EsHighlightFields(String preTags, String postTags) {
        super();
        this.preTags = preTags;
        this.postTags = postTags;
    }

    /**
     * @return the preTags
     */
    public String getPreTags() {
        return preTags;
    }

    /**
     * @param preTags the preTags to set
     */
    public void setPreTags(String preTags) {
        this.preTags = preTags;
    }

    /**
     * @return the postTags
     */
    public String getPostTags() {
        return postTags;
    }

    /**
     * @param postTags the postTags to set
     */
    public void setPostTags(String postTags) {
        this.postTags = postTags;
    }

    /**
     * @return the fragmentSize
     */
    public int getFragmentSize() {
        return fragmentSize;
    }

    /**
     * @param fragmentSize the fragmentSize to set
     */
    public void setFragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    /**
     * @return the numOfFragments
     */
    public int getNumOfFragments() {
        return numOfFragments;
    }

    /**
     * @param numOfFragments the numOfFragments to set
     */
    public void setNumOfFragments(int numOfFragments) {
        this.numOfFragments = numOfFragments;
    }

    /**
     * @return the fields
     */
    public List<String> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * @return the requireFieldMatch
     */
    public boolean getRequireFieldMatch() {
        return requireFieldMatch;
    }

    /**
     * @param requireFieldMatch the requireFieldMatch to set
     */
    public void setRequireFieldMatch(boolean requireFieldMatch) {
        this.requireFieldMatch = requireFieldMatch;
    }

    public EsHighlightFields addField(String field) {
        this.fields.add(field);
        return this;
    }

}
