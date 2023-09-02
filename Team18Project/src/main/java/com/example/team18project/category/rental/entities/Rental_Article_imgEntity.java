package com.example.team18project.category.rental.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rental_article_img")
public class Rental_Article_imgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rental_article_id")
    private Rental_ArticleEntity rentalArticle;

    private String img_url;
}
