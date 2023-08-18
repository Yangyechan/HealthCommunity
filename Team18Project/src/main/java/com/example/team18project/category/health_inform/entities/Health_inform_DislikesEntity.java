package com.example.team18project.category.health_inform.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "health_inform_dislikes")
public class Health_inform_DislikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "health_inform_article_id")
    private Health_Inform_ArticleEntity healthInformArticle;
}
