package com.example.team18project.category.bulk_inform.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bulk_inform_comment")
public class Bulk_Inform_CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "bulk_inform_article_id")
    private Bulk_Inform_ArticleEntity bulk_inform_article;

    private String content;

    private LocalDateTime created_at;
}
