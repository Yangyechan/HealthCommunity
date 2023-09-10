package com.example.team18project.category.gym.dto;

import lombok.Data;

@Data // 관리자용
public class Gym_Post_Dto {

    private String location;

    private String title; // 헬스장 이름

    private String content; // 헬스장 정보

    private String username; // 관장 ID

    private double x; // 헬스장 위치 좌표

    private double y; // 헬스장 위치 좌표

}
