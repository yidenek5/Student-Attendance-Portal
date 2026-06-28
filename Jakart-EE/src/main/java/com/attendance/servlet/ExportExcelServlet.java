package com.attendance.servlet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceSession;
import com.attendance.model.AttendanceSummary;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExportExcelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("teacherId") == null) {
            resp.sendRedirect(req.getContextPath() + "/teacher-login.jsp");
            return;
        }

        String reportType = req.getParameter("report");
        if (reportType == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "attendance-report.xlsx";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (XSSFWorkbook workbook = new XSSFWorkbook(); OutputStream out = resp.getOutputStream()) {
            int classId = 0;
            String classIdValue = req.getParameter("classId");
            if (classIdValue != null && !classIdValue.isBlank()) {
                try {
                    classId = Integer.parseInt(classIdValue);
                } catch (NumberFormatException ignored) {
                }
            }
            if ("semester".equals(reportType)) {
                List<AttendanceSummary> report = classId > 0
                        ? AttendanceRecordDAO.getSemesterReport((String) session.getAttribute("teacherId"), classId, null)
                        : AttendanceRecordDAO.getSemesterReport((String) session.getAttribute("teacherId"), null);
                XSSFSheet sheet = workbook.createSheet("Semester Summary");
                int rowNum = 0;
                Row header = sheet.createRow(rowNum++);
                header.createCell(0).setCellValue("Student ID");
                header.createCell(1).setCellValue("Student Name");
                header.createCell(2).setCellValue("Present");
                header.createCell(3).setCellValue("Absent");
                header.createCell(4).setCellValue("Attendance %");
                for (AttendanceSummary r : report) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(r.getStudentId());
                    row.createCell(1).setCellValue(r.getStudentName());
                    row.createCell(2).setCellValue(r.getPresentCount());
                    row.createCell(3).setCellValue(r.getAbsentCount());
                    row.createCell(4).setCellValue(String.format("%.2f", r.getAttendancePercentage()));
                }
            } else if ("absence".equals(reportType)) {
                List<AttendanceSummary> warnings = classId > 0
                        ? AttendanceRecordDAO.getAbsenceWarnings((String) session.getAttribute("teacherId"), classId)
                        : AttendanceRecordDAO.getAbsenceWarnings((String) session.getAttribute("teacherId"));
                XSSFSheet sheet = workbook.createSheet("Absence Warnings");
                int rowNum = 0;
                Row header = sheet.createRow(rowNum++);
                header.createCell(0).setCellValue("Student ID");
                header.createCell(1).setCellValue("Student Name");
                header.createCell(2).setCellValue("Present");
                header.createCell(3).setCellValue("Absent");
                header.createCell(4).setCellValue("Attendance %");
                for (AttendanceSummary r : warnings) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(r.getStudentId());
                    row.createCell(1).setCellValue(r.getStudentName());
                    row.createCell(2).setCellValue(r.getPresentCount());
                    row.createCell(3).setCellValue(r.getAbsentCount());
                    row.createCell(4).setCellValue(String.format("%.2f", r.getAttendancePercentage()));
                }
            } else if ("session".equals(reportType)) {
                String sessionIdValue = req.getParameter("sessionId");
                int sessionId = Integer.parseInt(sessionIdValue);
                AttendanceSession attendanceSession = AttendanceSessionDAO.findById(sessionId);
                XSSFSheet sheet = workbook.createSheet("Session " + sessionId);
                int rowNum = 0;
                Row header = sheet.createRow(rowNum++);
                header.createCell(0).setCellValue("Student ID");
                header.createCell(1).setCellValue("Student Name");
                header.createCell(2).setCellValue("Status");
                header.createCell(3).setCellValue("Marked At");
                List<AttendanceRecord> records = AttendanceRecordDAO.getRecordsBySession(sessionId);
                for (AttendanceRecord record : records) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(record.getStudentId());
                    row.createCell(1).setCellValue(record.getStudentName());
                    row.createCell(2).setCellValue(record.getStatus());
                    row.createCell(3).setCellValue(record.getMarkedAt().toString());
                }
            }
            workbook.write(out);
        }
    }
}
