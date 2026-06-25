package com.attendance.model;

public class Enrollment {
    private int enrollmentId;
    private int classId;
    private String studentId;

    public Enrollment() {
    }

    public Enrollment(int enrollmentId, int classId, String studentId) {
        this.enrollmentId = enrollmentId;
        this.classId = classId;
        this.studentId = studentId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", classId=" + classId +
                ", studentId='" + studentId + '\'' +
                '}';
    }
}
