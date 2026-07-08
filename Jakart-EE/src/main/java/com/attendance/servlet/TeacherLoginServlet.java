package com.attendance.servlet;

import java.io.IOException;

import com.attendance.dao.TeacherDAO;
import com.attendance.model.Teacher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class TeacherLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("redirect", req.getParameter("redirect"));
        req.getRequestDispatcher("/teacher-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String teacherId = trim(req.getParameter("teacherId"));
        String password = trim(req.getParameter("password"));

        if (teacherId.isBlank() || password.isBlank()) {
            req.setAttribute("error", "Please enter both Teacher ID and password.");
            req.getRequestDispatcher("/teacher-login.jsp").forward(req, resp);
            return;
        }

        Teacher teacher = TeacherDAO.validateTeacher(teacherId, password);
        if (teacher == null) {
            req.setAttribute("error", "Invalid credentials. Please try again.");
            req.getRequestDispatcher("/teacher-login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("teacherId", teacher.getTeacherId());
        session.setAttribute("teacherName", teacher.getFullName());
        session.setAttribute("teacherDepartment", teacher.getDepartment());

        String redirect = req.getParameter("redirect");
        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) {
            resp.sendRedirect(req.getContextPath() + redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
