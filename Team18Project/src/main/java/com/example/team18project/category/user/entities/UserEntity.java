package com.example.team18project.category.user.entities;

import com.example.team18project.category.gym.entities.GymEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  // 아이디

    @Column(nullable = false)
    private String password;  // 비밀번호

    @Column(nullable = false, unique = true)
    private String nickname;  // 별명

    private String address;   // 주소

    private String profile_img; // 개인 프로필

    private String email;  //  ex) example@naver.com

    private String phone;  // 010-1234-5678

    private String birth;  // 8자리 EX) 19981212

    private String gender; // W / M

    private String grade;  // 이건 알고리즘으로 switch case 문으로 구현할 예정 등급제

    private String border; // 테두리 1. 일반회원 검은색 2. 전문가 빨간색 3. 관리자 초록색

    private String role;   // 1. 일반회원 2. 트레이너 3. 관장  4. 관리자

    @OneToMany(mappedBy = "user")
    private List<GymEntity> gyms = new ArrayList<>();

}
