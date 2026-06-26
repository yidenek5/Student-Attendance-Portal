package com.attendance.model;

public class Student {
    private String studentId;
    private String fullName;
    private String department;
    private int yearLevel;
    private String section;

    public Student() {
    }

    public Student(String studentId, String fullName, String department, int yearLevel, String section) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.department = department;
        this.yearLevel = yearLevel;
        this.section = section;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", yearLevel=" + yearLevel +
                ", section='" + section + '\'' +
                '}';
    }
}