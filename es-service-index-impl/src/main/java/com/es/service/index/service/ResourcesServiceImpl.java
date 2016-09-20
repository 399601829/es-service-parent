package com.es.service.index.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.es.service.common.type.IndexType;
import com.es.service.index.bean.ResourcesTO;
import com.es.service.index.common.lock.IndexUpdateLock;
import com.es.service.index.core.IndexServiceSupport;

/**
 * 
 * 资源索引服务
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
@Service("resourcesService")
public class ResourcesServiceImpl extends IndexServiceSupport<ResourcesTO> implements
        IndexService<ResourcesTO> {

    @IndexUpdateLock(indexType = IndexType.RESOURCES, isFull = true)
    @Override
    public int doFullIndex() {
        log.info("doFullIndex start ...");
        return super.doFullIndex();
    }

    @IndexUpdateLock(indexType = IndexType.RESOURCES)
    @Override
    public int doIncreamentIndex(long specifiedTime) {
        log.info("doIncreamentIndex start ...");
        return super.doIncreamentIndex(specifiedTime);
    }

    @IndexUpdateLock(indexType = IndexType.RESOURCES)
    @Override
    public int doIncreamentIndex() {
        // TODO Auto-generated method stub
        return doIncreamentIndex(0);
    }

    @Override
    public void delDoc(String docId) {
        log.info("delDoc start ...");
        super.delDoc(docId);
    }

    @Override
    public void updateDoc(ResourcesTO to) {
        log.info("updateDoc start ...");
        super.updateDoc(to);
    }

    @Override
    public void updateDoc(List<ResourcesTO> tos) {
        log.info("updateDoc start ...");
        super.updateDoc(tos);
    }

}
