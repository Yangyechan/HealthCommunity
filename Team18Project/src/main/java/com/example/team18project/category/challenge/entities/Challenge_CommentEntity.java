package com.example.team18project.category.challenge.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "challenge_comment")
public class Challenge_CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "challenge_article_id")
    private Challenge_ArticleEntity challenge_article;

    private String content;

    private String created_at;
}
