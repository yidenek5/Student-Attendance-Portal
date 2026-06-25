package com.attendance.model;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private int recordId;
    private int sessionId;
    private String studentId;
    private String studentName;
    private String status;
    private LocalDateTime markedAt;

    public AttendanceRecord() {
    }

    public AttendanceRecord(int recordId, int sessionId, String studentId, String studentName, String status, LocalDateTime markedAt) {
        this.recordId = recordId;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.markedAt = markedAt;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "recordId=" + recordId +
                ", sessionId=" + sessionId +
                ", studentId='" + studentId + '\'' +
                ", status='" + status + '\'' +
                ", markedAt=" + markedAt +
                '}';
    }
}
