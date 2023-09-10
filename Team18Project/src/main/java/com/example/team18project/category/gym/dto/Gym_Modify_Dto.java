package com.example.team18project.category.gym.dto;

import lombok.Data;

@Data // 관리자, 관장, 트레이너 용도 
public class Gym_Modify_Dto {

    private String title; // 헬스장 이름

    private String content; // 헬스장 정보

    private String phone; // 헬스장 전화번호

    private String location;

}
