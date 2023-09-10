package com.example.team18project.category.challenge.dto;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class C_RespondArticlePageDto {
    private Long id;

    private String nickname;

    private String title;

    private LocalDateTime created_at;

    private Integer views;

    public static C_RespondArticlePageDto fromEntity(Challenge_ArticleEntity entity) {
        C_RespondArticlePageDto dto = new C_RespondArticlePageDto();

        dto.setId(entity.getId());

        dto.setNickname(entity.getUser().getNickname());

        dto.setTitle(entity.getTitle());

        dto.setCreated_at(entity.getCreated_at());

        dto.setViews(entity.getViews());

        return dto;
    }
}
