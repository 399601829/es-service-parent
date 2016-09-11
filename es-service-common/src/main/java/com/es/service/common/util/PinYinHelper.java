package com.es.service.common.util;

import java.util.List;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.client.ESClient;

/**
 * 汉语拼音处理，中文字符转拼音
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月23日
 * 
 */
public class PinYinHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 
     */
    private static volatile PinYinHelper handler;

    /**
     * pinyin配置
     */
    private HanyuPinyinOutputFormat format;

    private PinYinHelper() {
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
    }

    /**
     * 单例
     * 
     * @return
     */
    public static PinYinHelper getInstance() {
        if (handler == null) {
            synchronized (PinYinHelper.class) {
                if (handler == null) {
                    handler = new PinYinHelper();
                }
            }
        }
        return handler;
    }

    /**
     * 分词
     * 
     * @param str
     * @return
     */
    public List<String> analyze(String str) {
        AnalyzeRequest request = new AnalyzeRequest(str).analyzer("ik").index("test");
        AnalyzeResponse ar = ESClient.getClient().admin().indices().analyze(request).actionGet();
        List<String> analyzeTokens = Lists.newArrayList();
        for (AnalyzeToken at : ar.getTokens()) {
            analyzeTokens.add(at.getTerm());
        }
        return analyzeTokens;
    }

    /**
     * 分词并获取全拼，返回字符串以逗号间隔
     * 
     * @param strs
     * @return
     */
    public String getAnalyzePinYin(String strs) {
        Set<String> set = getPinYin_Index(strs);
        return StringUtils.join(set, ",");
    }

    /**
     * 分词并获取首字符，返回字符串以逗号间隔
     * 
     * @param strs
     * @return
     */
    public String getAnalyzePinYinPrefix(String strs) {
        Set<String> set = getPinYinPrefix_Index(strs);
        return StringUtils.join(set, ",");
    }

    /**
     * 解析词条拼音
     * 
     * @param word
     * @return
     */
    public Set<String> getPinYin_Index(String word) {
        Set<String> results = Sets.newHashSet();
        List<String> words = analyze(word);
        if (!words.contains(word)) {
            words.add(word);
        }

        String pinYin;
        for (String w : words) {
            pinYin = getPinYin(w);
            if (StringUtils.isNotEmpty(pinYin)) {
                results.add(pinYin);
            }
        }
        return results;
    }

    /**
     * 解析词条拼音首字母
     * 
     * @param word
     * @return
     */
    public Set<String> getPinYinPrefix_Index(String word) {
        Set<String> results = Sets.newHashSet();
        List<String> words = analyze(word);
        if (!words.contains(word)) {
            words.add(word);
        }

        String prefixPinYin;
        for (String w : words) {
            prefixPinYin = getPinYinPrefix(w);
            if (StringUtils.isNotEmpty(prefixPinYin)) {
                results.add(prefixPinYin);
            }
        }
        return results;
    }

    /**
     * 将汉字解析成拼音,取首字母，英文和数字不变
     * 
     * @param word
     * @return
     */
    public String getPinYinPrefix(String word) {
        char[] ch = word.trim().toCharArray();
        StringBuilder rs = new StringBuilder();
        try {
            String s_ch;
            String[] temp;
            for (int i = 0; i < ch.length; i++) {
                s_ch = Character.toString(ch[i]);
                if (s_ch.matches("[\u4e00-\u9fa5]+")) {
                    // 汉字
                    temp = PinyinHelper.toHanyuPinyinStringArray(ch[i], format);
                    if (null != temp && temp.length > 0) {
                        rs.append(temp[0].charAt(0));
                    }
                } else if (s_ch.matches("[\u0030-\u0039]+")) {
                    // 0-9
                    rs.append(s_ch);
                } else if (s_ch.matches("[\u0041-\u005a]+") || s_ch.matches("[\u0061-\u007a]+")) {
                    // a-zA-Z
                    rs.append(s_ch);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return rs.toString();
    }

    /**
     * 传入要分析的上行词，全拼解析，只解析汉字，英文，数字
     * 
     * @param word
     * @return
     */
    public String getPinYin(String word) {
        char[] ch = word.trim().toCharArray();
        StringBuilder rs = new StringBuilder();
        try {
            if (ch.length > 40) {
                return PinyinHelper.toHanyuPinyinString(word, format, " ");
            }
            // 解析
            String s_ch;
            String[] temp;
            for (int i = 0; i < ch.length; i++) {
                s_ch = Character.toString(ch[i]);
                if (s_ch.matches("[\u4e00-\u9fa5]+")) {
                    // 汉字
                    temp = PinyinHelper.toHanyuPinyinStringArray(ch[i], format);
                    if (null != temp && temp.length > 0) {
                        rs.append(temp[0]);
                    }
                } else if (s_ch.matches("[\u0030-\u0039]+")) {
                    // 0-9
                    rs.append(s_ch);
                } else if (s_ch.matches("[\u0041-\u005a]+") || s_ch.matches("[\u0061-\u007a]+")) {
                    // a-zA-Z
                    rs.append(s_ch);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return rs.toString();
    }

    public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
        String s1 = "大话西游";
        String s2 = getInstance().getAnalyzePinYin(s1) + ","
                + getInstance().getAnalyzePinYinPrefix(s1);
        System.out.println(s1);
        System.out.println(s2);
    }
}
