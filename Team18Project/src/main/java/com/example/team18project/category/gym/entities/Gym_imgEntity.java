package com.example.team18project.category.gym.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "gym_img")
public class Gym_imgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private GymEntity gym;

    private String img_url;
}
