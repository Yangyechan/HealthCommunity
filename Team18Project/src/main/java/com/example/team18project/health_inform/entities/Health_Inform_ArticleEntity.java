package com.example.team18project.health_inform.entities;

import com.example.team18project.diet_inform.entities.Diet_Inform_CommentEntity;
import com.example.team18project.diet_inform.entities.Diet_Inform_DislikesEntity;
import com.example.team18project.diet_inform.entities.Diet_Inform_LikesEntity;
import com.example.team18project.diet_inform.entities.Diet_Infrom_Article_imgEntity;
import com.example.team18project.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

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

    private String title;

    private String content;

    private String created_at;

    @OneToMany(mappedBy = "health_inform_article")
    private List<Health_Inform_CommentEntity> healthInformComments = new ArrayList<>();

    @OneToMany(mappedBy = "health_inform_article")
    private List<Health_inform_DislikesEntity> healthInformDislikes = new ArrayList<>();

    @OneToMany(mappedBy = "health_inform_article")
    private List<Diet_Infrom_Article_imgEntity> healthArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "health_inform_article")
    private List<Diet_Inform_LikesEntity> healthInformLikes = new ArrayList<>();
}
