package com.attendance.servlet;

import com.attendance.dao.AttendanceDeviceDAO;
import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.dao.EnrollmentDAO;
import com.attendance.dao.StudentDAO;
import com.attendance.model.AttendanceSession;
import com.attendance.model.Student;
import com.attendance.util.AttendanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MarkAttendanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        AttendanceService.completeExpiredSessions();
        req.getRequestDispatcher("/mark.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        AttendanceService.completeExpiredSessions();

        String studentId = trimParameter(req.getParameter("studentId"));
        String sessionCode = trimParameter(req.getParameter("sessionCode"));

        String message;
        if (studentId.isBlank() || sessionCode.isBlank()) {
            message = "Student ID and session code are required.";
        } else {
            AttendanceSession session = AttendanceSessionDAO.findByCode(sessionCode);
            if (session == null) {
                message = "No session found for the provided code.";
            } else if (!session.isActive()) {
                message = "The session is no longer active. Attendance is closed.";
            } else {
                Student student = StudentDAO.findById(studentId);
                String deviceId = trimParameter(req.getParameter("deviceId"));
                if (student == null) {
                    message = "Student ID not found. Please check your ID.";
                } else if (!EnrollmentDAO.isStudentEnrolled(session.getClassId(), studentId)) {
                    message = "You are not enrolled in the class for this session.";
                } else if (deviceId.isBlank()) {
                    message = "Device registration failed. Please use a supported browser and try again.";
                } else if (AttendanceDeviceDAO.isDeviceRegisteredToAnotherStudent(session.getSessionId(), deviceId, studentId)) {
                    message = "This device is already registered to another student for this session.";
                } else if (AttendanceDeviceDAO.isStudentRegisteredOnDifferentDevice(session.getSessionId(), studentId, deviceId)) {
                    message = "This student has already registered attendance from a different device for this session.";
                } else if (AttendanceRecordDAO.exists(session.getSessionId(), studentId)) {
                    message = "Attendance already marked for this student.";
                } else {
                    boolean success = AttendanceRecordDAO.markPresent(session.getSessionId(), studentId, deviceId);
                    if (success) {
                        message = "Attendance marked successfully for " + student.getFullName() + ".";
                    } else {
                        message = "Unable to mark attendance due to a system error.";
                    }
                }
            }
        }

        req.setAttribute("message", message);
        req.getRequestDispatcher("/mark.jsp").forward(req, resp);
    }

    private String trimParameter(String value) {
        return value == null ? "" : value.trim();
    }
}
