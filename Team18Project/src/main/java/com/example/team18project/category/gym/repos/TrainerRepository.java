package com.example.team18project.category.gym.repos;

import com.example.team18project.category.gym.entities.Trainer_boardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer_boardEntity, Long> {
}
