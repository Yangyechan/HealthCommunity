package com.example.team18project.category.rental.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rental_comment")
public class Rental_CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime created_at;

    @ManyToOne
    @JoinColumn(name = "rental_article_id")
    private Rental_ArticleEntity rentalArticle;
}
