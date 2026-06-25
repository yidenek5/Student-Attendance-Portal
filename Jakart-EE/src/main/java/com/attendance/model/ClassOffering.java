package com.attendance.model;

public class ClassOffering {
    private int classId;
    private int courseId;
    private String teacherId;
    private int semesterId;
    private String department;
    private int yearLevel;
    private String section;
    private String courseName;
    private String academicYear;
    private int semesterNumber;

    public ClassOffering() {
    }

    public ClassOffering(int classId, int courseId, String teacherId, int semesterId, String department, int yearLevel, String section) {
        this.classId = classId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.semesterId = semesterId;
        this.department = department;
        this.yearLevel = yearLevel;
        this.section = section;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    @Override
    public String toString() {
        return "ClassOffering{" +
                "classId=" + classId +
                ", courseId=" + courseId +
                ", teacherId='" + teacherId + '\'' +
                ", semesterId=" + semesterId +
                ", department='" + department + '\'' +
                ", yearLevel=" + yearLevel +
                ", section='" + section + '\'' +
                '}';
    }
}
