package com.example.team18project.category.gym.repos;

import com.example.team18project.category.gym.entities.GymEntity;
import com.example.team18project.category.gym.entities.Gym_rateEntity;
import com.example.team18project.category.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymRateRepository extends JpaRepository<Gym_rateEntity, Long> {
    Optional<Gym_rateEntity> findByUserAndGym(UserEntity user, GymEntity gym);
}
