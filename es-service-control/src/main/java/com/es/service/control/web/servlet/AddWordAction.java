package com.es.service.control.web.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.es.service.control.util.ExtWordProcessor;

/**
 * 
 * 添加word
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
public class AddWordAction extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 页面post方法
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String rtr_return = "当前时间：" + new Date().toLocaleString() + "<br/>写入词成功！现有词如下:<br/>";
        String tag = req.getParameter("tag");
        String action = req.getParameter("action");
        try {
            String word = URLDecoder.decode(req.getParameter("word"), "UTF-8");
            if (!ExtWordProcessor.write(tag, action, word)) {
                rtr_return = "当前时间：" + new Date().toLocaleString() + "<br/>写入词失败！现有词如下:<br>";
            }
            resp.getWriter().write(rtr_return + ExtWordProcessor.read(tag));
            resp.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
            rtr_return = "当前时间：" + new Date().toLocaleString() + "<br/>写入词失败！<br>";
            resp.getWriter().write(rtr_return);
            resp.getWriter().flush();
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException,
            IOException {
        super.service(req, res);
    }

}
