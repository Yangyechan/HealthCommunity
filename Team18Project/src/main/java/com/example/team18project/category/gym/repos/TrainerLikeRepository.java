package com.example.team18project.category.gym.repos;

import com.example.team18project.category.gym.entities.Trainer_boardEntity;
import com.example.team18project.category.gym.entities.Trainer_board_LikeEntity;
import com.example.team18project.category.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerLikeRepository extends JpaRepository<Trainer_board_LikeEntity, Long> {

    Optional<Trainer_board_LikeEntity> findByUserAndTrainer(UserEntity user, Trainer_boardEntity trainer);
}
