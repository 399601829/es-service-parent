package com.es.service.index.common.type;

import com.es.service.common.type.IndexType;
import com.es.service.index.bean.BaseTO;
import com.es.service.index.bean.ResourcesTO;
import com.es.service.index.core.IndexServiceSupport;
import com.es.service.index.dao.mapper.Mapper;
import com.es.service.index.dao.mapper.ResourcesMapper;
import com.es.service.index.provider.ApplicationContextProvider;
import com.es.service.index.service.ResourcesServiceImpl;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月28日
 * 
 */
public enum IndexServiceMapping {

    RESOUCES(IndexType.RESOURCES, ResourcesTO.class, ResourcesMapper.class,
            ResourcesServiceImpl.class);

    private IndexType indexType;

    private Class<?> beanCla;

    private Class<?> mapperCla;

    private Class<?> serviceCla;

    <T> IndexServiceMapping(IndexType indexType, Class<? extends BaseTO> beanCla,
            Class<? extends Mapper> mapperCla, Class<? extends IndexServiceSupport> serviceCla) {
        this.indexType = indexType;
        this.beanCla = beanCla;
        this.mapperCla = mapperCla;
        this.serviceCla = serviceCla;
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
     * @return the beanCla
     */
    public Class<?> getBeanCla() {
        return beanCla;
    }

    /**
     * @param beanCla the beanCla to set
     */
    public void setBeanCla(Class<?> beanCla) {
        this.beanCla = beanCla;
    }

    /**
     * @return the serviceCla
     */
    public Class<?> getServiceCla() {
        return serviceCla;
    }

    /**
     * @param serviceCla the serviceCla to set
     */
    public void setServiceCla(Class<?> serviceCla) {
        this.serviceCla = serviceCla;
    }

    /**
     * @return the mapperCla
     */
    public Class<?> getMapperCla() {
        return mapperCla;
    }

    /**
     * @param mapperCla the mapperCla to set
     */
    public void setMapperCla(Class<?> mapperCla) {
        this.mapperCla = mapperCla;
    }

    public <T> Mapper<T> getMapper() {
        return (Mapper<T>) ApplicationContextProvider.getBean(mapperCla);
    }

    public IndexServiceSupport<BaseTO> getService() {
        return (IndexServiceSupport<BaseTO>) ApplicationContextProvider.getBean(serviceCla);
    }

    /**
     * 根据beanclass获取
     * 
     * @param beanCla
     * @return
     */
    public static IndexServiceMapping getIndexServiceMapping(Class<?> beanCla) {
        IndexServiceMapping[] values = IndexServiceMapping.values();
        for (IndexServiceMapping val : values) {
            if (val.beanCla.equals(beanCla)) {
                return val;
            }
        }
        return null;
    }

    /**
     * 根据IndexType获取
     * 
     * @param beanCla
     * @return
     */
    public static IndexServiceMapping getIndexServiceMapping(IndexType indexType) {
        IndexServiceMapping[] values = IndexServiceMapping.values();
        for (IndexServiceMapping val : values) {
            if (val.indexType.getIndexNo() == indexType.getIndexNo()) {
                return val;
            }
        }
        return null;
    }

}
