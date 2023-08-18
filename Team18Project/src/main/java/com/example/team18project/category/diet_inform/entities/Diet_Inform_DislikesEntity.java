package com.example.team18project.category.diet_inform.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diet_inform_dislikes")
public class Diet_Inform_DislikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "diet_inform_article_id")
    private Diet_Inform_ArticleEntity dietInformArticle;
}
