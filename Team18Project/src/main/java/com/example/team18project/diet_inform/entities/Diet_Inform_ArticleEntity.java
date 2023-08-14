package com.example.team18project.diet_inform.entities;

import com.example.team18project.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

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

    private String created_at;
}
