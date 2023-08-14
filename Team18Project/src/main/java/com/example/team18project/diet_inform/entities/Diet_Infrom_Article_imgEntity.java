package com.example.team18project.diet_inform.entities;

import com.example.team18project.user.entities.UserEntity;
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
    private Diet_Inform_ArticleEntity dietInformArticle;

    private String img_url;
}
