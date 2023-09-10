package com.example.team18project.category.gym.repos;

import com.example.team18project.category.gym.entities.Trainer_board_CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerCommentRepository extends JpaRepository<Trainer_board_CommentEntity, Long> {
}
