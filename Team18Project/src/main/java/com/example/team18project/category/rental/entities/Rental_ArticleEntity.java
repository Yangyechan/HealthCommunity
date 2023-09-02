package com.example.team18project.category.rental.entities;

import com.example.team18project.category.health_inform.entities.Health_Inform_Article_imgEntity;
import com.example.team18project.category.rental.chat.jpa.ChatRoomEntity;
import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String tag; // 공지사항, 대여

    private LocalDateTime created_at;

    @OneToOne(mappedBy = "rentalArticle")
    private ChatRoomEntity chatRoom;

    @OneToMany(mappedBy = "rentalArticle")
    private List<Rental_Article_imgEntity> rentalArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "rentalArticle")
    private List<Rental_CommentEntity> rentalArticleComments = new ArrayList<>();

}
