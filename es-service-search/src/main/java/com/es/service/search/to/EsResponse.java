package com.es.service.search.to;

import java.io.Serializable;

/**
 * 搜索响应体
 * 
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月31日
 *
 */
public class EsResponse implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9007451504228640825L;

    /**
     * 总数
     */
    private int totalSize;

    /**
     * 搜索结果
     */
    private String jsonObject;

    /**
     * 返回码
     */
    private int resultCode;

    public EsResponse() {
        setJsonObject("[]");
        setTotalSize(0);
        setResultCode(200);
    }

    /**
     * @param totalSize
     * @param jsonObject
     * @param resultCode
     */
    public EsResponse(int totalSize, String jsonObject, int resultCode) {
        super();
        this.totalSize = totalSize;
        this.jsonObject = jsonObject;
        this.resultCode = resultCode;
    }

    /**
     * @return the totalSize
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * @param totalSize the totalSize to set
     */
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * @return the jsonObject
     */
    public String getJsonObject() {
        return jsonObject;
    }

    /**
     * @param jsonObject the jsonObject to set
     */
    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * @return the resultCode
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * @param resultCode the resultCode to set
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EsResponse [totalSize=" + totalSize + ", jsonObject=" + jsonObject
                + ", resultCode=" + resultCode + "]";
    }
   

}
