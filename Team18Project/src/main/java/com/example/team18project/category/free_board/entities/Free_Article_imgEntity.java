package com.example.team18project.category.free_board.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "free_article_img")
public class Free_Article_imgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "free_article_id")
    private Free_ArticleEntity freeArticle;

    private String imgUrl;
}
