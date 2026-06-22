package com.attendance.dao;

import com.attendance.model.AttendanceSession;
import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceSessionDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static AttendanceSession createSession(int classId, String sessionCode, int durationMinutes) {
        String sql = "INSERT INTO AttendanceSession (class_id, session_code, created_at, duration_minutes, active) VALUES (?, ?, ?, ?, TRUE)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, classId);
            statement.setString(2, sessionCode);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(4, durationMinutes);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    AttendanceSession session = new AttendanceSession();
                    session.setSessionId(generatedKeys.getInt(1));
                    session.setClassId(classId);
                    session.setSessionCode(sessionCode);
                    session.setCreatedAt(LocalDateTime.now());
                    session.setDurationMinutes(durationMinutes);
                    session.setActive(true);
                    return session;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean sessionCodeExists(String sessionCode) {
        String sql = "SELECT 1 FROM AttendanceSession WHERE session_code = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionCode);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<AttendanceSession> findActiveSessionsByTeacher(String teacherId) {
        String sql = "SELECT s.session_id, s.class_id, s.session_code, s.created_at, s.duration_minutes, s.active " +
                "FROM AttendanceSession s " +
                "JOIN ClassOffering c ON s.class_id = c.class_id " +
                "WHERE c.teacher_id = ? AND s.active = TRUE ORDER BY s.created_at DESC";
        return mapSessionList(sql, teacherId);
    }

    public static List<AttendanceSession> findByTeacher(String teacherId) {
        String sql = "SELECT s.session_id, s.class_id, s.session_code, s.created_at, s.duration_minutes, s.active " +
                "FROM AttendanceSession s " +
                "JOIN ClassOffering c ON s.class_id = c.class_id " +
                "WHERE c.teacher_id = ? ORDER BY s.created_at DESC";
        return mapSessionList(sql, teacherId);
    }

    public static AttendanceSession findByCode(String sessionCode) {
        String sql = "SELECT session_id, class_id, session_code, created_at, duration_minutes, active FROM AttendanceSession WHERE session_code = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionCode);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapSession(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AttendanceSession findById(int sessionId) {
        String sql = "SELECT session_id, class_id, session_code, created_at, duration_minutes, active FROM AttendanceSession WHERE session_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapSession(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<AttendanceSession> findExpiredActiveSessions() {
        String sql = "SELECT session_id, class_id, session_code, created_at, duration_minutes, active " +
                "FROM AttendanceSession " +
                "WHERE active = TRUE AND TIMESTAMPADD(MINUTE, duration_minutes, created_at) <= NOW()";
        List<AttendanceSession> sessions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                sessions.add(mapSession(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public static void deactivateSession(int sessionId) {
        String sql = "UPDATE AttendanceSession SET active = FALSE WHERE session_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<AttendanceSession> mapSessionList(String sql, String teacherId) {
        List<AttendanceSession> sessions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapSession(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessions;
    }

    private static AttendanceSession mapSession(ResultSet rs) throws Exception {
        AttendanceSession session = new AttendanceSession();
        session.setSessionId(rs.getInt("session_id"));
        session.setClassId(rs.getInt("class_id"));
        session.setSessionCode(rs.getString("session_code"));
        session.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        session.setDurationMinutes(rs.getInt("duration_minutes"));
        session.setActive(rs.getBoolean("active"));
        return session;
    }
}
