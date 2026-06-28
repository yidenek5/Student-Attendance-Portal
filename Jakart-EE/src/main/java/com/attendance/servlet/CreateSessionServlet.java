package com.attendance.servlet;

import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.dao.ClassOfferingDAO;
import com.attendance.model.AttendanceSession;
import com.attendance.model.ClassOffering;
import com.attendance.model.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

public class CreateSessionServlet extends HttpServlet {
    private static final String SESSION_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("teacherId") == null) {
            resp.sendRedirect(req.getContextPath() + "/teacher-login.jsp");
            return;
        }
        String teacherId = (String) session.getAttribute("teacherId");
        List<ClassOffering> classes = ClassOfferingDAO.findByTeacher(teacherId);
        req.setAttribute("classes", classes);
        req.getRequestDispatcher("create-session.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("teacherId") == null) {
            resp.sendRedirect(req.getContextPath() + "/teacher-login.jsp");
            return;
        }

        String teacherId = (String) session.getAttribute("teacherId");
        String classIdValue = req.getParameter("classId");
        String durationValue = req.getParameter("duration");

        String message;
        if (classIdValue == null || durationValue == null || classIdValue.isBlank() || durationValue.isBlank()) {
            message = "Please choose a class and enter session duration.";
        } else {
            try {
                int classId = Integer.parseInt(classIdValue);
                int durationMinutes = Math.max(10, Math.min(180, Integer.parseInt(durationValue)));
                ClassOffering chosenClass = ClassOfferingDAO.findById(classId);
                if (chosenClass == null || !teacherId.equals(chosenClass.getTeacherId())) {
                    message = "Invalid class selection or permission denied.";
                } else {
                    String sessionCode = generateUniqueCode();
                    AttendanceSession sessionRecord = AttendanceSessionDAO.createSession(classId, sessionCode, durationMinutes);
                    if (sessionRecord != null) {
                        message = "Attendance session created successfully. Session code: " + sessionCode;
                    } else {
                        message = "Unable to create session at this time. Please try again.";
                    }
                }
            } catch (NumberFormatException e) {
                message = "Duration and class selection must be valid numbers.";
            }
        }

        List<ClassOffering> classes = ClassOfferingDAO.findByTeacher(teacherId);
        req.setAttribute("classes", classes);
        req.setAttribute("message", message);
        req.getRequestDispatcher("create-session.jsp").forward(req, resp);
    }

    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder builder = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                builder.append(SESSION_CHARS.charAt(RANDOM.nextInt(SESSION_CHARS.length())));
            }
            code = builder.toString();
        } while (AttendanceSessionDAO.sessionCodeExists(code));
        return code;
    }
}
