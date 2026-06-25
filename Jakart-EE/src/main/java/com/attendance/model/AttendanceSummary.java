package com.attendance.model;

public class AttendanceSummary {
    private String studentId;
    private String studentName;
    private String className;
    private int presentCount;
    private int absentCount;
    private double attendancePercentage;

    public AttendanceSummary() {
    }

    public AttendanceSummary(String studentId, String studentName, String className, int presentCount, int absentCount, double attendancePercentage) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.attendancePercentage = attendancePercentage;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}
