package com.es.service.control.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.es.service.common.conf.Constants;
import com.google.common.collect.Lists;

/**
 * 增强分词库，写文件，如果为多机部署要对文件挂载nfs，否则会导致数据不一致
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月23日
 * 
 */
public class ExtWordProcessor {

    public static String remote_ext_dict_filename = "remote_ext_dict.txt";
    
    public static String remote_ext_stopwords_filename = "remote_ext_stopwords.txt";
    
    public static String remote_ext_synonym_filename = "remote_ext_synonym.txt";
    
    private String word_separator = "-";
    
    // 扩展词
    private File remote_ext_dict;

    // 停用词
    private File remote_ext_stopwords;
    
    // 同义词
    private File remote_ext_synonym;

    // 缓存数据
    private HashMap<File, ArrayList<String>> cache = new HashMap<File, ArrayList<String>>();

    private static volatile ExtWordProcessor instance;

    private ExtWordProcessor() {
        // 初始化文件
        initFile();
        // 初始化文件数据
        initFileData();
        // 从数据库加载数据
        initDBData();
    }

    public static ExtWordProcessor getInstance() {
        if (instance == null) {
            synchronized (ExtWordProcessor.class) {
                if (instance == null) {
                    instance = new ExtWordProcessor();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化文件
     */
    private void initFile() {
        // 初始化文件，不存在则创建
        remote_ext_dict = new File(Constants.es_index_info_dir
                + "/"+remote_ext_dict_filename);
        if (!remote_ext_dict.exists() || !remote_ext_dict.isFile()) {
            try {
                remote_ext_dict.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 初始化文件，不存在则创建
        remote_ext_stopwords = new File(Constants.es_index_info_dir
                + "/"+remote_ext_stopwords_filename);
        if (!remote_ext_stopwords.exists() || !remote_ext_stopwords.isFile()) {
            try {
                remote_ext_stopwords.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 初始化文件，不存在则创建
        remote_ext_synonym = new File(Constants.es_index_info_dir
                + "/"+remote_ext_synonym_filename);
        if (!remote_ext_synonym.exists() || !remote_ext_synonym.isFile()) {
            try {
                remote_ext_synonym.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化文件内容，主要从文件加载到cache
     */
    private void initFileData() {
        // 初始化缓存数据
        ArrayList<String> ext_dict = Lists.newArrayList();
        String content = readWordContent(remote_ext_dict);
        if (StringUtils.isNotBlank(content)) {
            String[] words = content.split("\n");
            for (String word : words) {
                if (StringUtils.isNotBlank(word)) {
                    ext_dict.add(word);
                }
            }
        }
        cache.put(remote_ext_dict, ext_dict);

        ArrayList<String> ext_stopwords = Lists.newArrayList();
        content = readWordContent(remote_ext_stopwords);
        if (StringUtils.isNotBlank(content)) {
            String[] words = content.split("\n");
            for (String word : words) {
                if (StringUtils.isNotBlank(word)) {
                    ext_stopwords.add(word);
                }
            }
        }
        cache.put(remote_ext_stopwords, ext_stopwords);
        
        ArrayList<String> ext_synonym = Lists.newArrayList();
        content = readWordContent(remote_ext_synonym);
        if (StringUtils.isNotBlank(content)) {
            String[] words = content.split("\n");
            for (String word : words) {
                if (StringUtils.isNotBlank(word)) {
                    ext_synonym.add(word);
                }
            }
        }
        cache.put(remote_ext_synonym, ext_synonym);
    }

    /**
     * 初始化db内容，预留，可以从数据库加载内容到文件
     * 
     * @param file
     */
    private void initDBData() {
        // int startIndex = 0;
        // int endIndex = 1000;
        // ArrayList<Object> list = null;
        // do {
        // list = DB.getDate(startIndex, endIndex);
        // if (list != null && list.size() > 0) {
        // long lastId = list.get(list.size() - 1).getId();
        // for (Object o : list) {
        // write(remote_ext_dict,o.getName());
        // }
        // startIndex = (int) lastId;
        // }
        // } while (list != null && list.size() > 0);

        // startIndex = 0;
        // do {
        // list = DB.getDate(startIndex, endIndex);
        // if (list != null && list.size() > 0) {
        // long lastId = list.get(list.size() - 1).getId();
        // for (Object o : list) {
        // write(remote_ext_dict,o.getName());
        // }
        // startIndex = (int) lastId;
        // }
        // } while (list != null && list.size() > 0);
        
        // startIndex = 0;
        // do {
        // list = DB.getDate(startIndex, endIndex);
        // if (list != null && list.size() > 0) {
        // long lastId = list.get(list.size() - 1).getId();
        // for (Object o : list) {
        // write(remote_ext_dict,o.getName());
        // }
        // startIndex = (int) lastId;
        // }
        // } while (list != null && list.size() > 0);

    }

    /**
     * 写数据
     * 
     * @param file
     * @param conent
     */
    private synchronized boolean write(File file, String action, String conent) {
        // 或者传入数据为空
        if (StringUtils.isBlank(conent)) {
            return false;
        }
        // 处理数据
        StringBuilder text = new StringBuilder();
        String[] words = conent.split(word_separator);
        ArrayList<String> list = cache.get(file);
        for (String word : words) {
            if ("delete".equals(action)) {
                // 删除数据
                list.remove(word);
            } else if ("add".equals(action)) {
                // 判断word是否已存在（文件中已有词汇a，数据库来源中也有词汇a或者后台手动添加了词汇a）
                if (list.contains(word)) {
                    continue;
                }
                list.add(word);
                text.append(word).append("\n");
            }
        }

        // 删除操作
        if ("delete".equals(action)) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            for (String word : list) {
                text.append(word).append("\n");
            }
        }

        // 写数据到文件
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),
                    "utf-8"));
            out.write(text.toString());
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 读取文件内容
     * 
     * @param file
     * @return
     */
    private String readWordContent(File file) {
        Long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 返回文件内容,默认编码
        return "";
    }

    /**
     * 返回文件，根据外部标签
     * 
     * @param tag
     * @return
     */
    private File getFile(String tag) {
        if (remote_ext_dict_filename.equals(tag)) {
            return remote_ext_dict;
        }
        if (remote_ext_stopwords_filename.equals(tag)) {
            return remote_ext_stopwords;
        }
        if (remote_ext_synonym_filename.equals(tag)) {
            return remote_ext_synonym;
        }
        return null;
    }

    /**
     * 写入词
     * 
     * @param tag 文件名
     * @param action 动作（add|delete）
     * @param conent
     * @return
     */
    public static boolean write(String tag, String action, String conent) {
        ExtWordProcessor instance = getInstance();
        File file = instance.getFile(tag);
        return instance.write(file, action, conent);
    }

    /**
     * 读出词
     * 
     * @param tag
     * @param conent
     * @return
     */
    public static String read(String tag) {
        ExtWordProcessor instance = getInstance();
        return instance.readWordContent(instance.getFile(tag));
    }

    /**
     * 返回文件最后修改时间
     * 
     * @param tag
     * @return
     */
    public static long getLastModified(String tag) {
        return getInstance().getFile(tag).lastModified();
    }
}
