package com.example.team18project.category.health_inform.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "health_inform_article_img")
public class Health_Inform_Article_imgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "health_inform_article_id")
    private Health_Inform_ArticleEntity health_inform_article;

    private String img_url;
}
