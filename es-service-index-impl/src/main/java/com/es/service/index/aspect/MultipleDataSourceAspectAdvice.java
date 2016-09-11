package com.es.service.index.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.es.service.index.dao.route.DataSourceRoute;
import com.es.service.index.dao.route.MultipleDataSource;

/**
 * 数据源路由切面
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
@Component
@Aspect
public class MultipleDataSourceAspectAdvice {

    @Around("execution(* com.es.service.index.dao.mapper.*.*(..))")
    public Object doAround(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature joinPointObject = (MethodSignature) jp.getSignature();
        Method method = joinPointObject.getMethod();
        DataSourceRoute annotation = method.getAnnotation(DataSourceRoute.class);
        annotation = annotation != null ? annotation : method.getDeclaringClass().getAnnotation(
                DataSourceRoute.class);
        if (null == annotation) {
            MultipleDataSource.setDataSourceKey("resources");
        } else {
            MultipleDataSource.setDataSourceKey(annotation.value().getKey());
        }
        return jp.proceed();
    }
}
