package com.attendance.model;

import java.time.LocalDateTime;

public class AttendanceSession {
    private int sessionId;
    private int classId;
    private String sessionCode;
    private LocalDateTime createdAt;
    private int durationMinutes;
    private boolean active;

    public AttendanceSession() {
    }

    public AttendanceSession(int sessionId, int classId, String sessionCode, LocalDateTime createdAt, int durationMinutes, boolean active) {
        this.sessionId = sessionId;
        this.classId = classId;
        this.sessionCode = sessionCode;
        this.createdAt = createdAt;
        this.durationMinutes = durationMinutes;
        this.active = active;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "AttendanceSession{" +
                "sessionId=" + sessionId +
                ", classId=" + classId +
                ", sessionCode='" + sessionCode + '\'' +
                ", createdAt=" + createdAt +
                ", durationMinutes=" + durationMinutes +
                ", active=" + active +
                '}';
    }
}
