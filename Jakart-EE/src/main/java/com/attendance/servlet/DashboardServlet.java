package com.attendance.servlet;

import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.dao.ClassOfferingDAO;
import com.attendance.dao.EnrollmentDAO;
import com.attendance.model.AttendanceSession;
import com.attendance.model.ClassOffering;
import com.attendance.model.Teacher;
import com.attendance.util.AttendanceService;
import com.attendance.dao.TeacherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("teacherId") == null) {
            resp.sendRedirect(req.getContextPath() + "/teacher-login.jsp");
            return;
        }

        String teacherId = (String) session.getAttribute("teacherId");
        AttendanceService.completeExpiredSessions();

        Teacher teacher = TeacherDAO.findById(teacherId);
        List<ClassOffering> classes = ClassOfferingDAO.findByTeacher(teacherId);
        List<AttendanceSession> activeSessions = AttendanceSessionDAO.findActiveSessionsByTeacher(teacherId);
        List<AttendanceSession> allSessions = AttendanceSessionDAO.findByTeacher(teacherId);

        Set<String> studentSet = new HashSet<>();
        classes.forEach(offering -> studentSet.addAll(
                EnrollmentDAO.getStudentsByClass(offering.getClassId()).stream().map(s -> s.getStudentId()).toList()
        ));

        req.setAttribute("teacher", teacher);
        req.setAttribute("classes", classes);
        req.setAttribute("activeSessions", activeSessions);
        req.setAttribute("sessionCount", allSessions.size());
        req.setAttribute("classCount", classes.size());
        req.setAttribute("studentCount", studentSet.size());
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
