package com.attendance.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.isEmpty() || path.equals("/")
                || path.equals("/index.jsp")
                || path.equals("/teacher-login.jsp")
                || path.equals("/mark.jsp")
                || path.equals("/login")
                || path.equals("/mark")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("teacherId") == null) {
            String redirectTarget = path;
            if (req.getQueryString() != null && !req.getQueryString().isBlank()) {
                redirectTarget += "?" + req.getQueryString();
            }
            String loginUrl = req.getContextPath() + "/teacher-login.jsp?redirect=" +
                    URLEncoder.encode(redirectTarget, StandardCharsets.UTF_8);
            resp.sendRedirect(loginUrl);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
