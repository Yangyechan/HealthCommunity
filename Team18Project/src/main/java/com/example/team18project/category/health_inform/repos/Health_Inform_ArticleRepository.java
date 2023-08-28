package com.example.team18project.category.health_inform.repos;

import com.example.team18project.category.health_inform.entities.Health_Inform_ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Health_Inform_ArticleRepository extends JpaRepository<Health_Inform_ArticleEntity, Long> {

    // 작성자 이름으로 게시물 목록 가져오기
    Page<Health_Inform_ArticleEntity> findByUser_UsernameIgnoreCaseContaining(String username, Pageable pageable);

    // 태그로 게시물 목록 가져오기
    Page<Health_Inform_ArticleEntity> findByTagIgnoreCaseContaining(String tag, Pageable pageable);

    // 제목으로 게시물 목록 가져오기
    Page<Health_Inform_ArticleEntity> findByTitleIgnoreCaseContaining(String title, Pageable pageable);

    Page<Health_Inform_ArticleEntity> findByTag(String tag, Pageable pageable);
    Page<Health_Inform_ArticleEntity> findByTagNot(String tag, Pageable pageable);

}
