package com.example.team18project.category.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserForm {
    @NotBlank
    private String username;
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String passwordCheck;
}
