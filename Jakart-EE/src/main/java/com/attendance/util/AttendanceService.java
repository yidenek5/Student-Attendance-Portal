package com.attendance.util;

import java.util.List;
import java.util.Set;

import com.attendance.dao.AttendanceDeviceDAO;
import com.attendance.dao.AttendanceRecordDAO;
import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.dao.EnrollmentDAO;
import com.attendance.model.AttendanceSession;
import com.attendance.model.Student;

public final class AttendanceService {

    private AttendanceService() {
    }

    public static void completeExpiredSessions() {
        List<AttendanceSession> expiredSessions = AttendanceSessionDAO.findExpiredActiveSessions();
        for (AttendanceSession session : expiredSessions) {
            int sessionId = session.getSessionId();
            int classId = session.getClassId();
            List<Student> enrolledStudents = EnrollmentDAO.getStudentsByClass(classId);
            Set<String> presentIds = AttendanceRecordDAO.getPresentStudentIds(sessionId);
            for (Student student : enrolledStudents) {
                if (!presentIds.contains(student.getStudentId())) {
                    AttendanceRecordDAO.markAbsent(sessionId, student.getStudentId());
                }
            }
            AttendanceSessionDAO.deactivateSession(sessionId);
            AttendanceDeviceDAO.clearDeviceRegistrations(sessionId);
        }
    }
}
