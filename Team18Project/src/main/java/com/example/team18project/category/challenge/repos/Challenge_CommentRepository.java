package com.example.team18project.category.challenge.repos;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Challenge_CommentRepository extends JpaRepository<Challenge_CommentEntity, Long> {
    List<Challenge_CommentEntity> findAllByChallengeArticle(Challenge_ArticleEntity entity);
}
