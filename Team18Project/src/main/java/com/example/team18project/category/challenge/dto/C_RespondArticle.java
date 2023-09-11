package com.example.team18project.category.challenge.dto;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_Article_imgEntity;
import com.example.team18project.category.challenge.entities.Challenge_CommentEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class C_RespondArticle {
    private String nickname;

    private String title;

    private String content;

    private List<String> articleImagesUrl;

    private List<C_CommentList> comments;

    private Integer likes;

    private LocalDateTime created_at;

    private Integer views;

    public static C_RespondArticle fromEntity(Challenge_ArticleEntity entity) {
        C_RespondArticle dto = new C_RespondArticle();

        dto.setNickname(entity.getUser().getNickname());

        dto.setTitle(entity.getTitle());

        dto.setContent(entity.getContent());

        List<String> images = new ArrayList<>();
        for (Challenge_Article_imgEntity image : entity.getChallengeArticleImgs()) {
            images.add(image.getImg_url());
        }
        dto.setArticleImagesUrl(images);

        List<C_CommentList> comments = new ArrayList<>();
        for (Challenge_CommentEntity comment : entity.getChallengeComments()) {
            C_CommentList targetComments = new C_CommentList();
            targetComments.setNickname(comment.getUser().getNickname());
            targetComments.setContent(comment.getContent());
            comments.add(targetComments);
        }
        dto.setComments(comments);

        dto.setCreated_at(entity.getCreated_at());

        dto.setLikes(entity.getChallengeLikes().size());

        dto.setViews(entity.getViews());

        return dto;
    }
}
