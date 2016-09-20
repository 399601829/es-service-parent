package com.es.service.common.type;

import java.io.Serializable;

/**
 * 索引更新事件
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月1日
 *
 */
public class Event<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4012465801085536964L;

    /*
     * 事件动作
     */
    private Action action;

    /**
     * 索引
     */
    private IndexType indexType;

    /**
     * 事件源数据,可以为id，也可以为bean
     */
    private T eventSource;

    /**
     * 
     */
    public Event() {
        super();
    }

    public Event(Action action, IndexType indexType, T eventSource) {
        if (action == null || indexType == null || eventSource == null) {
            throw new RuntimeException("action or indexType or eventSource is not null !");
        }
        this.action = action;
        this.indexType = indexType;
        this.eventSource = eventSource;
    }

    /**
     * 
     * 事件动作
     * 
     * @author hailin0@yeah.net
     * @createDate 2016年9月1日
     *
     */
    public static enum Action {
        CREATE("create"), 
        UPDATE("update"), 
        DELETE("delete"), 
        FULL_INDEX("fullIndex"), 
        INCREAMENT_INDEX("IncreamentIndex");
        /*
         * 事件动作
         */
        private String action;

        Action(String action) {
            this.action = action;
        }
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(Action action) {
        this.action = action;
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
     * @return the eventSource
     */
    public T getEventSource() {
        return eventSource;
    }

    /**
     * @param eventSource the eventSource to set
     */
    public void setEventSource(T eventSource) {
        this.eventSource = eventSource;
    }

}
