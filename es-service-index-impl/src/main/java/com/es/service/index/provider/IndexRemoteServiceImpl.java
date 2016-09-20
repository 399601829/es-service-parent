package com.es.service.index.provider;

import java.util.List;

import org.springframework.stereotype.Service;

import com.es.service.common.type.Event;
import com.es.service.index.IndexRemoteService;
import com.es.service.index.bean.BaseTO;
import com.es.service.index.common.type.IndexServiceMapping;
import com.es.service.index.service.IndexService;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月24日
 * 
 */
@Service("indexRemoteService")
public class IndexRemoteServiceImpl implements IndexRemoteService {

    @Override
    public String monitor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> boolean updateIndex(Event<T> event) {
        IndexServiceMapping indexServiceMapping = IndexServiceMapping.getIndexServiceMapping(event
                .getIndexType());
        IndexService<BaseTO> service = indexServiceMapping.getService();
        switch (event.getAction()) {
        case CREATE:
        case UPDATE:
            if (event.getEventSource() instanceof List) {
                List<BaseTO> list = (List) event.getEventSource();
                service.updateDoc(list);
            } else if (event.getEventSource() instanceof BaseTO) {
                BaseTO to = (BaseTO) event.getEventSource();
                service.updateDoc(to);
            } else {
                Object query = indexServiceMapping.getMapper().query(event.getEventSource());
                service.updateDoc((BaseTO) query);
            }
            break;
        case DELETE:
            String docId = String.valueOf(event.getEventSource());
            service.delDoc(docId);
            break;
        case FULL_INDEX:
            service.doFullIndex();
            break;
        case INCREAMENT_INDEX:
            service.doIncreamentIndex();
            break;
        default:
            break;
        }
        return true;

    }
}
