package com.practicaldime.zesty.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class HeadersDump extends HandlerFilter {

    private static final Logger LOG = LoggerFactory.getLogger(HeadersDump.class);

    public boolean before(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //GET /background.png HTTP/1.0
        String method = request.getMethod();
        String url = request.getRequestURI();
        String protocol = request.getProtocol();
        LOG.debug("{} {} {}", method, url, protocol);
        LOG.debug("begin--************************************************************");
        for (Enumeration<String> iter = request.getHeaderNames(); iter.hasMoreElements(); ) {
            String header = iter.nextElement();
            LOG.debug("{}: {}", header, request.getHeader(header));
        }
        LOG.debug("finish-************************************************************");
        return true;
    }
}
