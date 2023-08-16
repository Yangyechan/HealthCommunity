package com.example.team18project.diet_inform.entities;

import com.example.team18project.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diet_inform_likes")
public class Diet_Inform_LikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "diet_inform_article_id")
    private Diet_Inform_ArticleEntity diet_inform_article;
}
