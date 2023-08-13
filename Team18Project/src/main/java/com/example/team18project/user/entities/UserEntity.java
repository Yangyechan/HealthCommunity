package com.example.team18project.user.entities;

import com.example.team18project.gym.entities.GymEntity;
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
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String address;

    private String profile_img;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    private String birth;

    private String gender;

    private String grade;

    private String border;

    private Integer role;

    private String verification_img;

    @OneToMany(mappedBy = "user")
    private List<GymEntity> gyms = new ArrayList<>();
}
