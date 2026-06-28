package com.attendance.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceSession;
import com.attendance.model.AttendanceSummary;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ExportPdfServlet extends HttpServlet {

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

        resp.setContentType("application/pdf");
        String fileName = "attendance-report.pdf";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (OutputStream outputStream = resp.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("University Attendance Report", titleFont));
            document.add(new Paragraph(" "));

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
                document.add(new Paragraph("Semester Attendance Summary", headerFont));
                writeSummaryTable(document, report);
            } else if ("absence".equals(reportType)) {
                List<AttendanceSummary> warnings = classId > 0
                        ? AttendanceRecordDAO.getAbsenceWarnings((String) session.getAttribute("teacherId"), classId)
                        : AttendanceRecordDAO.getAbsenceWarnings((String) session.getAttribute("teacherId"));
                document.add(new Paragraph("Absence Warning Summary", headerFont));
                writeSummaryTable(document, warnings);
            } else if ("session".equals(reportType)) {
                String sessionIdValue = req.getParameter("sessionId");
                int sessionId = Integer.parseInt(sessionIdValue);
                AttendanceSession attendanceSession = AttendanceSessionDAO.findById(sessionId);
                if (attendanceSession == null) {
                    document.add(new Paragraph("Session not found."));
                } else {
                    document.add(new Paragraph("Session Code: " + attendanceSession.getSessionCode(), headerFont));
                    document.add(new Paragraph("Created At: " + attendanceSession.getCreatedAt(), headerFont));
                    List<AttendanceRecord> records = AttendanceRecordDAO.getRecordsBySession(sessionId);
                    writeSessionTable(document, records);
                }
            }
            document.close();
        } catch (DocumentException | NumberFormatException e) {
            throw new ServletException(e);
        }
    }

    private void writeSummaryTable(Document document, List<AttendanceSummary> report) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addHeaderCell(table, "Student ID");
        addHeaderCell(table, "Student Name");
        addHeaderCell(table, "Present");
        addHeaderCell(table, "Absent");
        addHeaderCell(table, "Attendance %");
        for (AttendanceSummary row : report) {
            table.addCell(row.getStudentId());
            table.addCell(row.getStudentName());
            table.addCell(String.valueOf(row.getPresentCount()));
            table.addCell(String.valueOf(row.getAbsentCount()));
            table.addCell(String.format("%.2f", row.getAttendancePercentage()));
        }
        document.add(table);
    }

    private void writeSessionTable(Document document, List<AttendanceRecord> records) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        addHeaderCell(table, "Student ID");
        addHeaderCell(table, "Student Name");
        addHeaderCell(table, "Status");
        addHeaderCell(table, "Marked At");
        for (AttendanceRecord record : records) {
            table.addCell(record.getStudentId());
            table.addCell(record.getStudentName());
            table.addCell(record.getStatus());
            table.addCell(record.getMarkedAt().toString());
        }
        document.add(table);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }
}
