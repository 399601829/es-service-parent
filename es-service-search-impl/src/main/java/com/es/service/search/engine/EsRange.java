package com.es.service.search.engine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年9月2日
 * 
 */
public class EsRange {

    // 1 to 10 or 1:10
    private static Pattern pattern2 = Pattern.compile("(.+?)?\\s*(?:\\:| TO | to )\\s*(.+)?");

    public static String[] rangeAdapter(String range) {
        Matcher matcher = pattern2.matcher(range);
        if (matcher.find()) {
            String first = matcher.group(1) == null ? null : matcher.group(1);
            String second = matcher.group(2) == null ? null : matcher.group(2);
            return new String[] {
                    first != null ? first : StringUtils.isNumeric(second) ? "-999999999" : " ",
                    second != null ? second : StringUtils.isNumeric(first) ? "999999999" : "~" };
        }
        return null;
    }

    private static Pattern pattern3 = Pattern
            .compile("(\\d{4}-\\d{1,2}-\\d{1,2}\\s*(?:\\d{1,2}:\\d{1,2}:\\d{1,2})?)?\\s*(?:\\:\\:| TO | to )\\s*(\\d{4}-\\d{1,2}-\\d{1,2}\\s*(?:\\d{1,2}:\\d{1,2}:\\d{1,2})?)?");

    public static Long[] dateAdapter(String range) {
        Matcher matcher = pattern3.matcher(range);
        boolean flag = false;
        if (matcher.find()) {
            Date beginDate = new Date(0);// "1970-01-01 01:00:00";
            Date endDate = new Date();
            for (int i = 0; i < matcher.groupCount(); i++) {
                // System.out.println(matcher.group(i+1));
                if (matcher.group(i + 1) != null) {
                    flag = true;
                    if (matcher.group(i + 1).length() < 11) {
                        if (i + 1 == matcher.groupCount()) {
                            // 后面的时间
                            endDate = String2Date(matcher.group(i + 1) + " 23:59:59");
                        } else {
                            // 前面的时间
                            beginDate = String2Date(matcher.group(i + 1) + " 00:00:00");
                        }
                    } else {
                        if (i + 1 == matcher.groupCount()) {
                            endDate = String2Date(matcher.group(i + 1));
                        } else {
                            beginDate = String2Date(matcher.group(i + 1));
                        }
                    }

                }

            }
            if (flag) {
                return new Long[] { beginDate.getTime(), endDate.getTime() };
            }

        }
        return null;
    }

    public static Date String2Date(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (date == null) {
                throw new Exception("输入参数不能为空{{错误}}");
            } else {
                return sdf.parse(date);
            }
        } catch (Exception ignore) {
        }
        return null;
    }
}
