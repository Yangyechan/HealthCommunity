package com.example.team18project.user.entities;

import com.example.team18project.gym.entities.GymEntity;
import com.example.team18project.gym.entities.Gym_rateEntity;
import com.example.team18project.gym.entities.Trainer_boardEntity;
import com.example.team18project.rental.entities.Rental_ArticleEntity;
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

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private GymEntity gym;

    @OneToMany(mappedBy = "user")
    private List<Gym_rateEntity> gym_rates = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Trainer_boardEntity> trainer_boards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rental_ArticleEntity> rentalArticles = new ArrayList<>();
}
