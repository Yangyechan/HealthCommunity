package com.example.team18project.category.gym.entities;


import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TrainerComment")
public class Trainer_board_CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer_boardEntity trainer;

    private String comment;

    private LocalDateTime createdAt;

}
