package com.attendance.model;

import java.time.LocalDateTime;

public class Session {
    private String code;
    private LocalDateTime expiryTime;

    public Session(String code, LocalDateTime expiryTime) {
        this.code = code;
        this.expiryTime = expiryTime;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}