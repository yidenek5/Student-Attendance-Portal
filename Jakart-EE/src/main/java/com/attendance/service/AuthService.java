package com.attendance.service;

import com.attendance.dao.TeacherDAO;
import com.attendance.model.Teacher;

public class AuthService {

    public static Teacher authenticate(String teacherId, String password) {
        if (teacherId == null || password == null) return null;
        return TeacherDAO.validateTeacher(teacherId.trim(), password.trim());
    }
}
