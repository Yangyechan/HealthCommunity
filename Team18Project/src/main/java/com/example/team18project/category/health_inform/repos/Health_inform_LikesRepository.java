package com.example.team18project.category.health_inform.repos;

import com.example.team18project.category.health_inform.entities.Health_Inform_LikesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Health_inform_LikesRepository extends JpaRepository<Health_Inform_LikesEntity, Long> {

    Optional<Health_Inform_LikesEntity> findByUserIdAndHealthInformArticleId(Long userId, Long articleId);

    void deleteByHealthInformArticleIdAndUserId(Long articleId, Long userId);
}
