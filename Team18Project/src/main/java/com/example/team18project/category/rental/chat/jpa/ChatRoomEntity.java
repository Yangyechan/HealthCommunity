package com.example.team18project.category.rental.chat.jpa;


import com.example.team18project.category.rental.entities.Rental_ArticleEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_room")
@Data
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;

    @OneToOne
    @JoinColumn(name = "rental_article_id")
    private Rental_ArticleEntity rentalArticle;
}
