package com.example.team18project.health_inform.entities;

import com.example.team18project.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "health_inform_likes")
public class Health_Inform_LikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "health_inform_article_id")
    private Health_Inform_ArticleEntity health_inform_article;
}
