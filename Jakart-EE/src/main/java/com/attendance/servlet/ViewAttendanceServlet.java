package com.attendance.servlet;

import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.dao.ClassOfferingDAO;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceSession;
import com.attendance.model.ClassOffering;
import com.attendance.util.AttendanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class ViewAttendanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("teacherId") == null) {
            resp.sendRedirect(req.getContextPath() + "/teacher-login.jsp");
            return;
        }
        AttendanceService.completeExpiredSessions();
        String teacherId = (String) session.getAttribute("teacherId");
        List<AttendanceSession> sessions = AttendanceSessionDAO.findByTeacher(teacherId);
        req.setAttribute("sessions", sessions);
        req.getRequestDispatcher("/view-attendance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("teacherId") == null) {
            resp.sendRedirect(req.getContextPath() + "/teacher-login.jsp");
            return;
        }
        AttendanceService.completeExpiredSessions();
        String teacherId = (String) session.getAttribute("teacherId");
        List<AttendanceSession> sessions = AttendanceSessionDAO.findByTeacher(teacherId);
        req.setAttribute("sessions", sessions);

        String sessionIdValue = req.getParameter("sessionId");
        if (sessionIdValue != null && !sessionIdValue.isBlank()) {
            try {
                int sessionId = Integer.parseInt(sessionIdValue);
                AttendanceSession selectedSession = AttendanceSessionDAO.findById(sessionId);
                if (selectedSession != null) {
                    List<AttendanceRecord> records = AttendanceRecordDAO.getRecordsBySession(sessionId);
                    int presentCount = AttendanceRecordDAO.getPresentCount(sessionId);
                    ClassOffering selectedClass = ClassOfferingDAO.findById(selectedSession.getClassId());
                    req.setAttribute("selectedSession", selectedSession);
                    req.setAttribute("selectedClass", selectedClass);
                    req.setAttribute("attendanceRecords", records);
                    req.setAttribute("presentCount", presentCount);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        req.getRequestDispatcher("view-attendance.jsp").forward(req, resp);
    }
}
