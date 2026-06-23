package com.attendance.dao;

import com.attendance.model.Teacher;
import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static Teacher validateTeacher(String teacherId, String password) {
        String sql = "SELECT teacher_id, full_name, email, password, department FROM Teacher WHERE teacher_id = ? AND password = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTeacher(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Teacher findById(String teacherId) {
        String sql = "SELECT teacher_id, full_name, email, password, department FROM Teacher WHERE teacher_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTeacher(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Teacher mapTeacher(ResultSet resultSet) throws Exception {
        return new Teacher(
                resultSet.getString("teacher_id"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("department")
        );
    }
}
