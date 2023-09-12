package com.example.team18project.category.user.repos;

import com.example.team18project.category.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    Optional<UserEntity> findByNickname(String nickname);
}
