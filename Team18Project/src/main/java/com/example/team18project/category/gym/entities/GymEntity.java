package com.example.team18project.category.gym.entities;
import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


// 관장이 우리 사이트 홍보물을 보고 전화를 한다. -> 우리가 고유번호를 생성해서 gym 테이블에 넣어준다.
@Data
@Entity
@Table(name = "gym")
public class GymEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 사용자가 보는 위치 (도로명)
    private String location;

    private String title; // 헬스장 이름

    private String content; // 헬스장 정보

    @Column(nullable = false)
    private double x; // 헬스장 위치 좌표
    @Column(nullable = false)
    private double y; // 헬스장 위치 좌표

    @Column(unique = true) // 헬스장 전화번호
    private String phone;

    @Column(nullable = false, unique = true) // 헬스장 고유번호
    private String identityCode;

    @OneToMany(mappedBy = "gym")// 관장, 트레이너
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "gym") // 평점
    private List<Gym_rateEntity> gymRates = new ArrayList<>();

    @OneToMany(mappedBy = "gym") // 댓글
    private List<Gym_rateEntity> gymComments = new ArrayList<>();

    @OneToMany(mappedBy = "gym")// 헬스장 사진
    private List<Gym_imgEntity> gymImages = new ArrayList<>();

    @OneToMany(mappedBy = "gym")// 트레이너 정보 게시판
    private List<Trainer_boardEntity> trainerBoards = new ArrayList<>();
}
