package com.example.team18project.gym.entities;

import com.example.team18project.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "gym")
public class GymEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private String created_at;

    private String identity_code;

    @OneToMany(mappedBy = "gym")
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "gym")
    private List<Gym_rateEntity> gym_rates = new ArrayList<>();

    @OneToMany(mappedBy = "gym")
    private List<Gym_imgEntity> gym_imgs = new ArrayList<>();

    @OneToMany(mappedBy = "gym")
    private List<Trainer_boardEntity> trainer_boards = new ArrayList<>();
}
