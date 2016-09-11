package com.test;

import com.es.service.index.common.lock.JedisManager;
import com.es.service.index.common.lock.JedisManagerFactory;

public class RedisTest {

    public static void main(String[] args) {
        JedisManager jm = JedisManagerFactory.getJedisManager();
        
        String string = jm.getSet("abc", "111");
        
        System.out.println(string);
    }

}
