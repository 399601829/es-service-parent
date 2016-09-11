package com.es.service.index.provider;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月24日
 * 
 */
public class DubboServiceProvider {

    private static Logger logger = LoggerFactory.getLogger(DubboServiceProvider.class);

    private static volatile boolean running;

    private static ClassPathXmlApplicationContext context;

    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        System.out.println("main.args" + Arrays.asList(args));
        // System.setProperty("dubbo.application.logger", "slf4j");
        lock.lock();
        try {

            if (context == null) {
                context = new ClassPathXmlApplicationContext(
                        new String[] { "spring-conf/es-service-index-provider.xml" });
                logger.info("esindexServiceProvider to start  !!!");
                running = true;
                String[] beans = context.getBeanDefinitionNames();
                for (String bean : beans) {
                    logger.debug(bean);
                }
                logger.info("indexServiceProvider start to finish !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        } finally {
            lock.unlock();
        }
        lock.lock();
        try {
            if (running && args != null && args.length > 0 && "stop".equalsIgnoreCase(args[0])) {
                running = false;
            }
        } finally {
            lock.unlock();
        }
        while (running) {
            try {
                Thread.currentThread().join(5000L);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }
}