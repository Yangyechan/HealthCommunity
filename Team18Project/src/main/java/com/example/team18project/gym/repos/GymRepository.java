package com.example.team18project.gym.repos;

import com.example.team18project.gym.entities.GymEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GymRepository extends JpaRepository<GymEntity, Long> {
     Optional<GymEntity> findByIdentityCode(String identityCode);
}

// 1. 이미 메인 브랜치에 있는 코드들을 주석하거나 없애고 merge를 한다
// 2. 너가 추가한 엔티티를 백업을 해놓고 버전 동기화를 하고 추가를 한다.