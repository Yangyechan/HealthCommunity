package com.example.team18project.category.diet_inform.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diet_inform_article_img")
public class Diet_Infrom_Article_imgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diet_inform_article_id")
    private Diet_Inform_ArticleEntity diet_inform_article;

    private String img_url;
}
