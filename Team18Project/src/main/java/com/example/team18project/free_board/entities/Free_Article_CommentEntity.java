package com.example.team18project.free_board.entities;

import com.example.team18project.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "free_article_comment")
public class Free_Article_CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "free_article_id")
    private Free_ArticleEntity freeArticle;

    private String title;

    private String content;

    private String created_at;
}
