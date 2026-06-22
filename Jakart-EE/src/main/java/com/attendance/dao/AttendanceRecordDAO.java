package com.attendance.dao;

import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceSummary;
import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendanceRecordDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static boolean exists(int sessionId, String studentId) {
        String sql = "SELECT 1 FROM AttendanceRecord WHERE session_id = ? AND student_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            statement.setString(2, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean markPresent(int sessionId, String studentId) {
        return markPresent(sessionId, studentId, null);
    }

    public static boolean markPresent(int sessionId, String studentId, String deviceId) {
        if (exists(sessionId, studentId) || deviceId == null || deviceId.isBlank()) {
            return false;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            if (AttendanceDeviceDAO.isDeviceRegisteredToAnotherStudent(connection, sessionId, deviceId, studentId)) {
                connection.rollback();
                return false;
            }
            if (AttendanceDeviceDAO.isStudentRegisteredOnDifferentDevice(connection, sessionId, studentId, deviceId)) {
                connection.rollback();
                return false;
            }
            if (!AttendanceDeviceDAO.registerDevice(connection, sessionId, deviceId, studentId)) {
                connection.rollback();
                return false;
            }
            String sql = "INSERT INTO AttendanceRecord (session_id, student_id, status, marked_at) VALUES (?, ?, 'PRESENT', ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, sessionId);
                statement.setString(2, studentId);
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                int rows = statement.executeUpdate();
                if (rows == 1) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean markAbsent(int sessionId, String studentId) {
        if (exists(sessionId, studentId)) {
            return false;
        }
        String sql = "INSERT INTO AttendanceRecord (session_id, student_id, status, marked_at) VALUES (?, ?, 'ABSENT', ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            statement.setString(2, studentId);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return statement.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Set<String> getPresentStudentIds(int sessionId) {
        String sql = "SELECT student_id FROM AttendanceRecord WHERE session_id = ? AND status = 'PRESENT'";
        Set<String> studentIds = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    studentIds.add(rs.getString("student_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentIds;
    }

    public static List<AttendanceRecord> getRecordsBySession(int sessionId) {
        String sql = "SELECT ar.record_id, ar.session_id, ar.student_id, ar.status, ar.marked_at, s.full_name " +
                "FROM AttendanceRecord ar " +
                "JOIN Student s ON ar.student_id = s.student_id " +
                "WHERE ar.session_id = ? ORDER BY ar.marked_at";
        List<AttendanceRecord> records = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setRecordId(rs.getInt("record_id"));
                    record.setSessionId(rs.getInt("session_id"));
                    record.setStudentId(rs.getString("student_id"));
                    record.setStudentName(rs.getString("full_name"));
                    record.setStatus(rs.getString("status"));
                    record.setMarkedAt(rs.getTimestamp("marked_at").toLocalDateTime());
                    records.add(record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    public static int getPresentCount(int sessionId) {
        String sql = "SELECT COUNT(*) AS present_count FROM AttendanceRecord WHERE session_id = ? AND status = 'PRESENT'";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("present_count");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<AttendanceSummary> getSemesterReport(String teacherId, String studentQuery) {
        return getSemesterReport(teacherId, 0, studentQuery);
    }

    public static List<AttendanceSummary> getSemesterReport(String teacherId, int classId, String studentQuery) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.student_id, s.full_name, ")
                .append("SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count, ")
                .append("SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count, ")
                .append("SUM(CASE WHEN ar.status IN ('PRESENT', 'ABSENT') THEN 1 ELSE 0 END) AS total_records ")
                .append("FROM AttendanceRecord ar ")
                .append("JOIN Student s ON ar.student_id = s.student_id ")
                .append("JOIN AttendanceSession ses ON ar.session_id = ses.session_id ")
                .append("JOIN ClassOffering c ON ses.class_id = c.class_id ")
                .append("WHERE c.teacher_id = ? ");
        if (classId > 0) {
            sql.append("AND c.class_id = ? ");
        }
        if (studentQuery != null && !studentQuery.isBlank()) {
            sql.append("AND (s.student_id LIKE ? OR s.full_name LIKE ?) ");
        }
        sql.append("GROUP BY s.student_id, s.full_name ")
                .append("ORDER BY absent_count DESC, present_count DESC");

        List<AttendanceSummary> summaries = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            statement.setString(index++, teacherId);
            if (classId > 0) {
                statement.setInt(index++, classId);
            }
            if (studentQuery != null && !studentQuery.isBlank()) {
                String pattern = "%" + studentQuery.trim() + "%";
                statement.setString(index++, pattern);
                statement.setString(index++, pattern);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int present = rs.getInt("present_count");
                    int absent = rs.getInt("absent_count");
                    int total = rs.getInt("total_records");
                    double percentage = total == 0 ? 0.0 : (present * 100.0) / total;
                    summaries.add(new AttendanceSummary(
                            rs.getString("student_id"),
                            rs.getString("full_name"),
                            "",
                            present,
                            absent,
                            percentage
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summaries;
    }

    public static List<AttendanceSummary> getAbsenceWarnings(String teacherId) {
        return getAbsenceWarnings(teacherId, 0);
    }

    public static List<AttendanceSummary> getAbsenceWarnings(String teacherId, int classId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.student_id, s.full_name, ")
                .append("SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count, ")
                .append("SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count, ")
                .append("SUM(CASE WHEN ar.status IN ('PRESENT', 'ABSENT') THEN 1 ELSE 0 END) AS total_records ")
                .append("FROM AttendanceRecord ar ")
                .append("JOIN Student s ON ar.student_id = s.student_id ")
                .append("JOIN AttendanceSession ses ON ar.session_id = ses.session_id ")
                .append("JOIN ClassOffering c ON ses.class_id = c.class_id ")
                .append("WHERE c.teacher_id = ? ");
        if (classId > 0) {
            sql.append("AND c.class_id = ? ");
        }
        sql.append("GROUP BY s.student_id, s.full_name ")
                .append("HAVING SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) >= 3 ")
                .append("ORDER BY absent_count DESC, present_count DESC");

        List<AttendanceSummary> warnings = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            statement.setString(index++, teacherId);
            if (classId > 0) {
                statement.setInt(index++, classId);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int present = rs.getInt("present_count");
                    int absent = rs.getInt("absent_count");
                    int total = rs.getInt("total_records");
                    double percentage = total == 0 ? 0.0 : (present * 100.0) / total;
                    warnings.add(new AttendanceSummary(
                            rs.getString("student_id"),
                            rs.getString("full_name"),
                            "",
                            present,
                            absent,
                            percentage
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warnings;
    }
}
