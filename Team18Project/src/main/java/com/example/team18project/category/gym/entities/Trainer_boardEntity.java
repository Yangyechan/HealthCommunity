package com.example.team18project.category.gym.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "trainer_board")
public class Trainer_boardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private GymEntity gym;

    private String title;

    private String content;

    @OneToMany(mappedBy = "trainer")// 트레이너 사진
    private List<Trainer_board_ImgEntity> trainerImages = new ArrayList<>();

    @OneToMany(mappedBy = "trainer")// 트레이너 댓글
    private List<Trainer_board_CommentEntity> trainerComments = new ArrayList<>();

    @OneToMany(mappedBy = "trainer")// 트레이너 좋아요
    private List<Trainer_board_LikeEntity> trainerLikes = new ArrayList<>();

}
