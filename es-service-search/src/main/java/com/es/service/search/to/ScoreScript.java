package com.es.service.search.to;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 修改得分
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月1日
 * 
 */
public class ScoreScript implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -787622865621913459L;

    /**
     * 脚本
     */
    private String script;

    /**
     * 脚本参数
     */
    private Map<String, Object> scriptParams;

    /**
     */
    public ScoreScript() {
        super();
    }

    /**
     * @param script
     */
    public ScoreScript(String script) {
        super();
        this.script = script;
    }

    /**
     * @param script
     * @param scriptParams
     */
    public ScoreScript(String script, Map<String, Object> scriptParams) {
        super();
        this.script = script;
        this.scriptParams = scriptParams;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the scriptParams
     */
    public Map<String, Object> getScriptParams() {
        return scriptParams;
    }

    /**
     * @param scriptParams the scriptParams to set
     */
    public void setScriptParams(Map<String, Object> scriptParams) {
        this.scriptParams = scriptParams;
    }

}
