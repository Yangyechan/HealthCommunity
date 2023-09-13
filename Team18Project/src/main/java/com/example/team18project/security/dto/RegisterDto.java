package com.example.team18project.security.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String passwordCheck;
    @NotBlank
    private String nickname;
    @NotBlank
    private String address;

    private String email;

    private String profile_img;

    private String phone;

    private String birth;

    private String gender;

    private String identityCode;  // 헬스장 고유번호

}
