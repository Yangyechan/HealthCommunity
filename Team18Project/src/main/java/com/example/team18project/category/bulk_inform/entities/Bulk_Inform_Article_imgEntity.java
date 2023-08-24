package com.example.team18project.category.bulk_inform.entities;

import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bulk_inform_article_img")
public class Bulk_Inform_Article_imgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bulk_inform_article_id")
    private Bulk_Inform_ArticleEntity bulkInformArticle;

    private String img_url;
}
