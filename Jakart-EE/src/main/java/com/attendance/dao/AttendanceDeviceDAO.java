package com.attendance.dao;

import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AttendanceDeviceDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static boolean isDeviceRegisteredToAnotherStudent(int sessionId, String deviceId, String studentId) {
        try (Connection connection = dataSource.getConnection()) {
            return isDeviceRegisteredToAnotherStudent(connection, sessionId, deviceId, studentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDeviceRegisteredToAnotherStudent(Connection connection, int sessionId, String deviceId, String studentId) {
        String sql = "SELECT student_id FROM AttendanceDevice WHERE session_id = ? AND device_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            statement.setString(2, deviceId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return !studentId.equals(rs.getString("student_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isStudentRegisteredOnDifferentDevice(int sessionId, String studentId, String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            return isStudentRegisteredOnDifferentDevice(connection, sessionId, studentId, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isStudentRegisteredOnDifferentDevice(Connection connection, int sessionId, String studentId, String deviceId) {
        String sql = "SELECT device_id FROM AttendanceDevice WHERE session_id = ? AND student_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            statement.setString(2, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String existingDeviceId = rs.getString("device_id");
                    return !deviceId.equals(existingDeviceId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registerDevice(Connection connection, int sessionId, String deviceId, String studentId) {
        String sql = "INSERT INTO AttendanceDevice (session_id, device_id, student_id) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE student_id = VALUES(student_id)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            statement.setString(2, deviceId);
            statement.setString(3, studentId);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void clearDeviceRegistrations(int sessionId) {
        String sql = "DELETE FROM AttendanceDevice WHERE session_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
