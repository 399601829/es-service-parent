package com.es.service.control.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.es.service.control.util.ExtWordProcessor;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
public class LoadWordAction extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /*
     * 缓存修改时间
     */
    private Map<String, Long> cache = new HashMap<String, Long>();

    /**
     * 页面post方法
     * <p>
     * Last-Modified,ETags两个http头的处理，以及按照ik规定的格式返回扩展词汇(词汇以\n分割)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        String tag = req.getParameter("tag");
        if (StringUtils.isBlank(tag)) {
            String uri = req.getRequestURI();
            tag = uri.endsWith(ExtWordProcessor.remote_ext_dict_filename) ? ExtWordProcessor.remote_ext_dict_filename
                    : ExtWordProcessor.remote_ext_stopwords_filename;
        }

        String txt = "";
        try {
            resp.setHeader("Last-Modified", ExtWordProcessor.getLastModified(tag) + "");
            resp.setHeader("ETags", ExtWordProcessor.getLastModified(tag) + "");
            cache.put(tag, ExtWordProcessor.getLastModified(tag));
            txt = ExtWordProcessor.read(tag);
        } catch (Exception e) {
            // 出错的情况 用上一次内存中的值
            resp.setHeader("Last-Modified", cache.get(tag) + "");
            resp.setHeader("ETags", cache.get(tag) + "");
        }
        resp.getWriter().write(txt);
        resp.getWriter().flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        doPost(req, resp);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException,
            IOException {
        super.service(req, res);
    }

}
