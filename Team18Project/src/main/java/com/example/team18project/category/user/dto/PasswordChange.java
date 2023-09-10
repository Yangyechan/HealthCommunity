package com.example.team18project.category.user.dto;

import lombok.Data;

@Data
public class PasswordChange {
    private String password; // 기존 비밀번호

    private String newPassword; // 변경 비밀번호

    private String newPasswordCheck; // 변경 비밀번호 일치 확인
}
