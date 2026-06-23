package com.attendance.dao;

import com.attendance.model.Student;
import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static Student findById(String studentId) {
        String sql = "SELECT student_id, full_name, department, year_level, section FROM Student WHERE student_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapStudent(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Student> findByClass(int classId) {
        String sql = "SELECT s.student_id, s.full_name, s.department, s.year_level, s.section " +
                "FROM Student s " +
                "JOIN Enrollment e ON s.student_id = e.student_id " +
                "WHERE e.class_id = ? ORDER BY s.student_id";
        List<Student> students = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public static List<Student> searchStudents(String query) {
        String sql = "SELECT student_id, full_name, department, year_level, section FROM Student " +
                "WHERE student_id LIKE ? OR full_name LIKE ? ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + query.trim() + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    private static Student mapStudent(ResultSet rs) throws Exception {
        return new Student(
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getString("department"),
                rs.getInt("year_level"),
                rs.getString("section")
        );
    }
}
