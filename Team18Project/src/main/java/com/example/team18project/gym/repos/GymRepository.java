package com.example.team18project.gym.repos;

import com.example.team18project.gym.entities.GymEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<GymEntity, Long> {
}
