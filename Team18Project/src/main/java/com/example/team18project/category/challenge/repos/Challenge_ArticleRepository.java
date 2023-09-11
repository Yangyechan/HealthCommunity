package com.example.team18project.category.challenge.repos;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.user.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Challenge_ArticleRepository extends JpaRepository<Challenge_ArticleEntity, Long> {
    Page<Challenge_ArticleEntity> findAllByTitleContaining(String keyword, Pageable pageable);
    Page<Challenge_ArticleEntity> findByTag(String tag, Pageable pageable);
    Page<Challenge_ArticleEntity> findByTagNot(String tag, Pageable pageable);
    List<Challenge_ArticleEntity> findAllByUser(UserEntity entity);
}
