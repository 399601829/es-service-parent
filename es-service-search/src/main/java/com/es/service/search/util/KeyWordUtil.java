package com.es.service.search.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月2日
 *
 */
public class KeyWordUtil {
    public static void main(String[] args) {
        String s = "dahuaxiyou dhxy dahuaxi dhx";
        List<String> list = processKeyWord(s);
        System.out.println(s);
    }

    /**
     * 将字符串按空格分隔,连续的数字+空格分隔为一个词
     * <p>
     * 
     * @param keyWord
     * @return
     */
    public static List<String> processKeyWord(String keyWord) {
        String[] keys = keyWord.trim().split("\\s+");
        List<String> keys_list = new ArrayList<String>();
        keys_list.add(keyWord);
        for (String key : keys) {
            if (keys_list.size() > 0 && NumberUtils.isNumber(key)) {
                String pre = keys_list.get(keys_list.size() - 1);
                if (NumberUtils.isNumber(pre.replace(" ", ""))) {
                    keys_list.set(keys_list.size() - 1, pre.concat(" ").concat(key));
                    continue;
                }
            }
            if (!keys_list.contains(key)) {
                keys_list.add(key);
            }
        }
        return keys_list;
    }

}
