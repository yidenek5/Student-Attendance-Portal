package com.attendance.service;

import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.dao.EnrollmentDAO;
import com.attendance.model.AttendanceSession;
import com.attendance.model.Student;

import java.util.List;
import java.util.Set;

public class AttendanceService {

    public static void finalizeExpiredSessions() {
        List<AttendanceSession> expired = AttendanceSessionDAO.findExpiredActiveSessions();
        for (AttendanceSession s : expired) {
            int sessionId = s.getSessionId();
            int classId = s.getClassId();
            List<Student> enrolled = EnrollmentDAO.getStudentsByClass(classId);
            Set<String> present = AttendanceRecordDAO.getPresentStudentIds(sessionId);
            for (Student st : enrolled) {
                if (!present.contains(st.getStudentId())) {
                    AttendanceRecordDAO.markAbsent(sessionId, st.getStudentId());
                }
            }
            AttendanceSessionDAO.deactivateSession(sessionId);
        }
    }
}
