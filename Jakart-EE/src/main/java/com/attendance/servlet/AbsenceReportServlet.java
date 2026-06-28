package com.attendance.servlet;

import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.dao.ClassOfferingDAO;
import com.attendance.model.AttendanceSummary;
import com.attendance.model.ClassOffering;
import com.attendance.util.AttendanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class AbsenceReportServlet extends HttpServlet {

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
        List<ClassOffering> classes = ClassOfferingDAO.findByTeacher(teacherId);
        String classIdValue = req.getParameter("classId");
        int selectedClassId = 0;
        if (classIdValue != null && !classIdValue.isBlank()) {
            try {
                selectedClassId = Integer.parseInt(classIdValue);
            } catch (NumberFormatException ignored) {
            }
        }
        List<AttendanceSummary> warnings = selectedClassId > 0
                ? AttendanceRecordDAO.getAbsenceWarnings(teacherId, selectedClassId)
                : List.of();
        req.setAttribute("classes", classes);
        req.setAttribute("selectedClassId", selectedClassId);
        req.setAttribute("warnings", warnings);
        req.getRequestDispatcher("/absence-report.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
