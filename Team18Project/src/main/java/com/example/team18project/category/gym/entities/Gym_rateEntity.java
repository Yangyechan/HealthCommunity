package com.example.team18project.category.gym.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "gym_rate")
public class Gym_rateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private GymEntity gym;

    private Integer rate;

    private String comment;
}
