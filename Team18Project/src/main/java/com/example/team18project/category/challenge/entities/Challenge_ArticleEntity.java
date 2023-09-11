package com.example.team18project.category.challenge.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "challenge_article")
public class Challenge_ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String tag; // 태그

    private String title;

    private String content;

    private LocalDateTime created_at;

    private Integer views;

    @OneToMany(mappedBy = "challengeArticle")
    private List<Challenge_Article_imgEntity> challengeArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "challengeArticle")
    private List<Challenge_CommentEntity> challengeComments = new ArrayList<>();

    @OneToMany(mappedBy = "challengeArticle")
    private List<Challenge_LikesEntity> challengeLikes = new ArrayList<>();
}
