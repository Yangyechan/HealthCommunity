package com.example.team18project.category.challenge.repos;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_Article_imgEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Challenge_Article_imgRepository extends JpaRepository<Challenge_Article_imgEntity, Long> {
    Optional<Challenge_Article_imgEntity> findByChallengeArticle(Challenge_ArticleEntity entity);
    List<Challenge_Article_imgEntity> findAllByChallengeArticle(Challenge_ArticleEntity entity);
}
