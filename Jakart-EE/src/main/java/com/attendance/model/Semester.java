package com.attendance.model;

public class Semester {
    private int semesterId;
    private String academicYear;
    private int semesterNumber;

    public Semester() {
    }

    public Semester(int semesterId, String academicYear, int semesterNumber) {
        this.semesterId = semesterId;
        this.academicYear = academicYear;
        this.semesterNumber = semesterNumber;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
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
        return "Semester{" +
                "semesterId=" + semesterId +
                ", academicYear='" + academicYear + '\'' +
                ", semesterNumber=" + semesterNumber +
                '}';
    }
}
