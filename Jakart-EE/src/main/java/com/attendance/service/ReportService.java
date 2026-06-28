package com.attendance.service;

import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.model.AttendanceSummary;

import java.util.List;

public class ReportService {

    public static List<AttendanceSummary> getSemesterReport(String teacherId, String search) {
        return AttendanceRecordDAO.getSemesterReport(teacherId, search);
    }

    public static List<AttendanceSummary> getAbsenceWarnings(String teacherId) {
        return AttendanceRecordDAO.getAbsenceWarnings(teacherId);
    }
}
