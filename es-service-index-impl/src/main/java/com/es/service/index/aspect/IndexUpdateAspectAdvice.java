package com.es.service.index.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.es.service.index.common.lock.IndexLockHelper;
import com.es.service.index.common.lock.IndexUpdateLock;

/**
 * 
 * 资源更新分布式锁切面
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月23日
 * 
 */
@Component
@Aspect
public class IndexUpdateAspectAdvice {

    public Logger log = LoggerFactory.getLogger(IndexUpdateAspectAdvice.class);

    @Around(value = "@annotation(com.es.service.index.common.lock.IndexUpdateLock)")
    public Object doAround(final ProceedingJoinPoint jp) throws Throwable {
        MethodSignature joinPointObject = (MethodSignature) jp.getSignature();
        Method method = joinPointObject.getMethod();
        // lock注解
        IndexUpdateLock annotation = method.getAnnotation(IndexUpdateLock.class);
        annotation = annotation != null ? annotation : method.getDeclaringClass().getAnnotation(
                IndexUpdateLock.class);

        if (annotation == null) {
            return jp.proceed();
        }

        // 加锁
        if (IndexLockHelper.lock(annotation)) {
            log.info("get lock success,indexname:{},type:{},isfull:{},isDelete:{}", annotation
                    .indexType().getIndexName(), annotation.indexType().getTypeName(), annotation
                    .isFull(), annotation.isDelete());
            try {
                return jp.proceed();
            } finally {
                IndexLockHelper.unLock(annotation);
            }
        }
        log.error("get lock fail,indexname:{},type:{},isfull:{},isDelete:{}", annotation
                .indexType().getIndexName(), annotation.indexType().getTypeName(), annotation
                .isFull(), annotation.isDelete());
        return 0;

    }
}
