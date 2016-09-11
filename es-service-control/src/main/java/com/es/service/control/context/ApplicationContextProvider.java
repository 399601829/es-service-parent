package com.es.service.control.context;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.es.service.common.conf.Constants;

/**
 * 监听类，用于Spring启动时setApplicationContext
 */
@Service
public class ApplicationContextProvider implements ApplicationContextAware {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContextProvider.class);

    /**
     * 上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        // glassfish特性，启动后调用system.gc执行下fullgc操作
        // System.gc();

        ApplicationContextProvider.applicationContext = applicationContext;

        // 打印出所有Bean实例的名称
        /*String[] beannames = applicationContext.getBeanDefinitionNames();
        for (int i = 0; beannames != null && i < beannames.length; i++) {
            LOG.info(String.format("instance of bean:::::::::::::::::::::%03d : %s", i + 1,
                    beannames[i]));
        }*/

        // 日志实现类
        //LOG.info("loggerImpl init sucess ! ------->" + LOG.getClass().getName());
        //LOG.info("-------------  start success ! ------------------");
        
        
        //初始化目录
        File es_dir = new File(Constants.es_index_info_dir);
        if(!es_dir.exists()){
            es_dir.mkdirs();
        }
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     * 
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 获取类型为requiredType的对象 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     * 
     * @param name bean注册名
     * @param requiredType 返回对象类型
     * @return Object 返回requiredType类型对象
     * @throws BeansException
     */
    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 获取类型为requiredType的对象 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     * 
     * @param requiredType 返回对象类型
     * @return Object 返回requiredType类型对象
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     * 
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     * 
     * @param name
     * @return boolean
     * @throws NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     * 
     * @param name
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(name);
    }
}