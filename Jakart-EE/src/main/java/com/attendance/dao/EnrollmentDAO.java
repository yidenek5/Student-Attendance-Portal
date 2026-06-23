package com.attendance.dao;

import com.attendance.model.Student;
import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static List<Student> getStudentsByClass(int classId) {
        String sql = "SELECT s.student_id, s.full_name, s.department, s.year_level, s.section " +
                "FROM Student s " +
                "JOIN Enrollment e ON s.student_id = e.student_id " +
                "WHERE e.class_id = ? ORDER BY s.student_id";
        List<Student> students = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getString("student_id"),
                            rs.getString("full_name"),
                            rs.getString("department"),
                            rs.getInt("year_level"),
                            rs.getString("section")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public static boolean isStudentEnrolled(int classId, String studentId) {
        String sql = "SELECT 1 FROM Enrollment WHERE class_id = ? AND student_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classId);
            statement.setString(2, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
