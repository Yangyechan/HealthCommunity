package com.example.team18project.category.free_board.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "free_article")
public class Free_ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    private String content;

    private String created_at;

    @OneToMany(mappedBy = "free_article")
    private List<Free_Article_CommentEntity> freeArticleComments = new ArrayList<>();

    @OneToMany(mappedBy = "free_article")
    private List<Free_Article_DislikesEntity> freeArticleDislikes = new ArrayList<>();

    @OneToMany(mappedBy = "free_article")
    private List<Free_Article_imgEntity> freeArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "free_article")
    private List<Free_Article_LikesEntity> freeArticleLikes = new ArrayList<>();
}
