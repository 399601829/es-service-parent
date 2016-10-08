package com.es.service.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

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
     * pinyin配置
     */
    private static HanyuPinyinOutputFormat format;

    static {
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
    }

    /**
     * 分词并获取全拼，返回字符串以逗号间隔
     * 
     * @param strs
     * @return
     */
    public static String getAnalyzePinYin(String strs) {
        Set<String> set = getPinYin_Index(strs);
        return StringUtils.join(set, ",");
    }

    /**
     * 分词并获取首字符，返回字符串以逗号间隔
     * 
     * @param strs
     * @return
     */
    public static String getAnalyzePinYinPrefix(String strs) {
        Set<String> set = getPinYinPrefix_Index(strs);
        return StringUtils.join(set, ",");
    }

    /**
     * 解析词条拼音
     * 
     * @param word
     * @return
     */
    public static Set<String> getPinYin_Index(String word) {
        Set<String> results = Sets.newHashSet();
        List<String> words = AnalyzeHelper.analyze(word);
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
    public static Set<String> getPinYinPrefix_Index(String word) {
        Set<String> results = Sets.newHashSet();
        List<String> words = AnalyzeHelper.analyze(word);
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
    public static String getPinYinPrefix(String word) {
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
    public static String getPinYin(String word) {
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

    /**
     * 传入要分析的上行词，全拼解析，只解析汉字，英文，数字（包含多音字）
     * 
     * @param word
     * @return
     */
    public static List<StringBuilder> getPinYinByMultitone(String word) {
        char[] ch = word.trim().toCharArray();
        List<StringBuilder> builds = Lists.newArrayList(new StringBuilder());
        try {
            if (ch.length > 40) {
                builds.add(new StringBuilder(PinyinHelper.toHanyuPinyinString(word, format, " ")));
                return builds;
            }
            // 解析
            String s_ch;
            Set<String> yin;
            for (int i = 0; i < ch.length; i++) {
                s_ch = Character.toString(ch[i]);
                yin = new HashSet<String>();
                if (s_ch.matches("[\u4e00-\u9fa5]+")) {
                    // 汉字
                    yin.addAll(Arrays.asList(PinyinHelper.toHanyuPinyinStringArray(ch[i], format)));
                } else if (s_ch.matches("[\u0030-\u0039]+")) {
                    // 0-9
                    yin.add(s_ch);
                } else if (s_ch.matches("[\u0041-\u005a]+") || s_ch.matches("[\u0061-\u007a]+")) {
                    // a-zA-Z
                    yin.add(s_ch);
                }

                for (int j = 0, len = builds.size(); j < len; j++) {
                    String temp = builds.get(j).toString();
                    int ycount = 0;
                    for (String y : yin) {
                        if (ycount > 0) {
                            builds.add(new StringBuilder(temp).append(y));
                        } else {
                            builds.get(j).append(y);
                        }
                        ycount++;
                    }
                }

            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return builds;
    }
    
    /**
     * 首字母解析，只解析汉字，英文，数字（包含多音字）
     * 
     * @param word
     * @return
     */
    public static List<StringBuilder> getPinYinPrefixByMultitone(String word) {
        char[] ch = word.trim().toCharArray();
        List<StringBuilder> builds = Lists.newArrayList(new StringBuilder());
        try {
            if (ch.length > 40) {
                builds.add(new StringBuilder(PinyinHelper.toHanyuPinyinString(word, format, " ")));
                return builds;
            }
            // 解析
            String s_ch;
            Set<String> yin;
            for (int i = 0; i < ch.length; i++) {
                s_ch = Character.toString(ch[i]);
                yin = new HashSet<String>();
                if (s_ch.matches("[\u4e00-\u9fa5]+")) {
                    // 汉字
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch[i], format);
                    for(String pinyin : pinyinArray){
                        yin.add(String.valueOf(pinyin.charAt(0)));
                    }
                } else if (s_ch.matches("[\u0030-\u0039]+")) {
                    // 0-9
                    yin.add(s_ch);
                } else if (s_ch.matches("[\u0041-\u005a]+") || s_ch.matches("[\u0061-\u007a]+")) {
                    // a-zA-Z
                    yin.add(s_ch);
                }
                
                for (int j = 0, len = builds.size(); j < len; j++) {
                    String temp = builds.get(j).toString();
                    int ycount = 0;
                    for (String y : yin) {
                        if (ycount > 0) {
                            builds.add(new StringBuilder(temp).append(y));
                        } else {
                            builds.get(j).append(y);
                        }
                        ycount++;
                    }
                }
                
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return builds;
    }

    public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
        // String s1 = "大话西游";
        // String s2 = getAnalyzePinYin(s1) + "," + getAnalyzePinYinPrefix(s1);
        // System.out.println(s1);
        // System.out.println(s2);
        //System.out.println(getPinYinByMultitone("阿调空调"));
        System.out.println(getPinYinPrefixByMultitone("阿调空调"));
    }
}
