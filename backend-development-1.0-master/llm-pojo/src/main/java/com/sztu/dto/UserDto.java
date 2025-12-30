package com.sztu.dto;

import java.io.Serializable;

public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String studentId;
    private String password;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
