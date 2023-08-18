package com.example.team18project.category.bulk_inform.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "bulk_inform_article")
public class Bulk_Inform_ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    private String content;

    private LocalDateTime created_at;

    @OneToMany(mappedBy = "bulkInformArticle")
    private List<Bulk_Inform_Article_imgEntity> bulkInformArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "bulkInformArticle")
    private List<Bulk_Inform_CommentEntity> bulkInformComments = new ArrayList<>();

    @OneToMany(mappedBy = "bulkInformArticle")
    private List<Bulk_Inform_LikesEntity> bulkInformLikes = new ArrayList<>();
}
