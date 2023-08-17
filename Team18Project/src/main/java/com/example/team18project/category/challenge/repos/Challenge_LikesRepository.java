package com.example.team18project.category.challenge.repos;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_LikesEntity;
import com.example.team18project.category.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Challenge_LikesRepository extends JpaRepository<Challenge_LikesEntity, Long> {
    Optional<Challenge_LikesEntity> findByUserAndArticle(UserEntity user, Challenge_ArticleEntity article);
}
