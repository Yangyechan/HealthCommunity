package com.example.team18project.category.challenge.dto;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.user.entities.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Challenge_ArticleDto {
//    private String nickname;
//
//    private String title;
//
//    private String content;
//
//    private LocalDateTime created_at;
//
//    public static Challenge_ArticleDto fromEntity(Challenge_ArticleEntity entity) {
//        Challenge_ArticleDto dto = new Challenge_ArticleDto();
//        dto.setNickname(entity.getUser().getNickname());
//        dto.setTitle(entity.getTitle());
//        dto.setContent(entity.getContent());
//        dto.setCreated_at(entity.getCreated_at());
//        return dto;
//    }

    @NotBlank
    private String tag;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
