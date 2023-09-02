package com.example.team18project.category.health_inform.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "health_inform_article")
public class Health_Inform_ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String tag; // 추가 기타 공지사항 다이어트 벌크업

    private LocalDateTime created_at;

    @OneToMany(mappedBy = "healthInformArticle")
    private List<Health_Inform_CommentEntity> healthInformComments = new ArrayList<>();


    @OneToMany(mappedBy = "healthInformArticle")
    private List<Health_Inform_Article_imgEntity> healthArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "healthInformArticle")
    private List<Health_Inform_LikesEntity> healthInformLikes = new ArrayList<>();
}
