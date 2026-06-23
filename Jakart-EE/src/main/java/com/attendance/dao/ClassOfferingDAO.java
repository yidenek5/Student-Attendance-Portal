package com.attendance.dao;

import com.attendance.model.ClassOffering;
import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClassOfferingDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static List<ClassOffering> findByTeacher(String teacherId) {
        String sql = "SELECT c.class_id, c.course_id, c.teacher_id, c.semester_id, c.department, c.year_level, c.section, " +
                "co.course_name, s.academic_year, s.semester_number " +
                "FROM ClassOffering c " +
                "JOIN Course co ON c.course_id = co.course_id " +
                "JOIN Semester s ON c.semester_id = s.semester_id " +
                "WHERE c.teacher_id = ? ORDER BY co.course_name, c.section";
        List<ClassOffering> classes = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ClassOffering offering = mapClassOffering(rs);
                    offering.setCourseName(rs.getString("course_name"));
                    offering.setAcademicYear(rs.getString("academic_year"));
                    offering.setSemesterNumber(rs.getInt("semester_number"));
                    classes.add(offering);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static ClassOffering findById(int classId) {
        String sql = "SELECT c.class_id, c.course_id, c.teacher_id, c.semester_id, c.department, c.year_level, c.section, " +
                "co.course_name, s.academic_year, s.semester_number " +
                "FROM ClassOffering c " +
                "JOIN Course co ON c.course_id = co.course_id " +
                "JOIN Semester s ON c.semester_id = s.semester_id " +
                "WHERE c.class_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    ClassOffering offering = mapClassOffering(rs);
                    offering.setCourseName(rs.getString("course_name"));
                    offering.setAcademicYear(rs.getString("academic_year"));
                    offering.setSemesterNumber(rs.getInt("semester_number"));
                    return offering;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ClassOffering mapClassOffering(ResultSet rs) throws Exception {
        ClassOffering offering = new ClassOffering();
        offering.setClassId(rs.getInt("class_id"));
        offering.setCourseId(rs.getInt("course_id"));
        offering.setTeacherId(rs.getString("teacher_id"));
        offering.setSemesterId(rs.getInt("semester_id"));
        offering.setDepartment(rs.getString("department"));
        offering.setYearLevel(rs.getInt("year_level"));
        offering.setSection(rs.getString("section"));
        return offering;
    }
}
