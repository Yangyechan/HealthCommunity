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

    @Column(nullable = false, unique = true)
    private String location;

    private String created_at;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String identityCode;

    @OneToMany(mappedBy = "gym")
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "gym")
    private List<Gym_rateEntity> gym_rates = new ArrayList<>();

    @OneToMany(mappedBy = "gym")
    private List<Gym_imgEntity> gym_imgs = new ArrayList<>();

    @OneToMany(mappedBy = "gym")
    private List<Trainer_boardEntity> trainer_boards = new ArrayList<>();
}
