package com.example.team18project.category.gym.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TrainerImages")
public class Trainer_board_ImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer_boardEntity trainer;

    private String imgUrl;

}
