package com.es.service.search.to;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 搜索结果脚本过滤
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月30日
 *
 */
public class EsFilterScript implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2219936750755330005L;

    /**
     * 脚本
     */
    private String script;

    /**
     * 脚本参数
     */
    private Map<String, Object> scriptParams = new HashMap<String, Object>();

    public EsFilterScript() {
    }

    public EsFilterScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Map<String, Object> getScriptParams() {
        return scriptParams;
    }

    public void setScriptParams(Map<String, Object> scriptParams) {
        this.scriptParams = scriptParams;
    }
}
