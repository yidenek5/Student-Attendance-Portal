package com.attendance.service;

import java.security.SecureRandom;

import com.attendance.dao.AttendanceSessionDAO;
import com.attendance.dao.ClassOfferingDAO;
import com.attendance.model.AttendanceSession;
import com.attendance.model.ClassOffering;

public class SessionService {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static AttendanceSession createSession(int classId, int durationMinutes, String teacherId) {
        ClassOffering offering = ClassOfferingDAO.findById(classId);
        if (offering == null || !teacherId.equals(offering.getTeacherId())) {
            return null;
        }
        String code = generateUniqueCode();
        return AttendanceSessionDAO.createSession(classId, code, durationMinutes);
    }

    private static String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
            code = sb.toString();
        } while (AttendanceSessionDAO.sessionCodeExists(code));
        return code;
    }
}
