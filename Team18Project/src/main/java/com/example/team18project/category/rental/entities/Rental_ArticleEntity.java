package com.example.team18project.category.rental.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rental_article")
public class Rental_ArticleEntity {
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
