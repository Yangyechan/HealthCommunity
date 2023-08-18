package com.example.team18project.category.diet_inform.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "diet_inform_article")
public class Diet_Inform_ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    private String content;

    private LocalDateTime created_at;

    @OneToMany(mappedBy = "dietInformArticle")
    private List<Diet_Inform_CommentEntity> dietInformComments = new ArrayList<>();

    @OneToMany(mappedBy = "dietInformArticle")
    private List<Diet_Inform_DislikesEntity> dietArticleDislikes = new ArrayList<>();

    @OneToMany(mappedBy = "dietInformArticle")
    private List<Diet_Infrom_Article_imgEntity> dietArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "dietInformArticle")
    private List<Diet_Inform_LikesEntity> dietArticleLikes = new ArrayList<>();
}
