package com.es.service.search.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月5日
 * 
 */
@Component
@Aspect
public class ResponseTimeAspectAdvice {

    private static Logger logger = LoggerFactory.getLogger("slowrequestLogger");

    @Around("execution(* com.es.service.search.provider.SearchRemoteServiceImpl.*(..))")
    public Object doAround(ProceedingJoinPoint jp) throws Throwable {

        MethodSignature joinPointObject = (MethodSignature) jp.getSignature();
        Method method = joinPointObject.getMethod();
        String className = jp.getSignature().getDeclaringType().getName();
        String methodName = method.getName();
        Object[] args = jp.getArgs();

        StringBuffer buffer = new StringBuffer("");
        for (Object object : args) {
            buffer.append(String.valueOf(object)).append(",");
        }
        Long start = System.currentTimeMillis();
        try {
            return jp.proceed();
        } finally {
            long costTime = System.currentTimeMillis() - start;
            if (logger.isDebugEnabled()) {
                logger.debug("do methodName over ,className={},methodName={},args={},costTime={}",
                        className, methodName, buffer.toString(), costTime);
            } else if (costTime > 100) {
                logger.error("do methodName over ,className={},methodName={},args={},costTime={}",
                        className, methodName, buffer.toString(), costTime);
            }
        }
    }
}
