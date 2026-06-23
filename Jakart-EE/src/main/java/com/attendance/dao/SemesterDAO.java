package com.attendance.dao;

import com.attendance.model.Semester;
import com.attendance.util.DBConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SemesterDAO {
    private static final DataSource dataSource = DBConnection.getDataSource();

    public static Semester findById(int semesterId) {
        String sql = "SELECT semester_id, academic_year, semester_number FROM Semester WHERE semester_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Semester(rs.getInt("semester_id"), rs.getString("academic_year"), rs.getInt("semester_number"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Semester> findAll() {
        String sql = "SELECT semester_id, academic_year, semester_number FROM Semester ORDER BY academic_year, semester_number";
        List<Semester> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Semester(rs.getInt("semester_id"), rs.getString("academic_year"), rs.getInt("semester_number")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
