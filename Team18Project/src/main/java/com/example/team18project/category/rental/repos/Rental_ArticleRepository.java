package com.example.team18project.category.rental.repos;

import com.example.team18project.category.rental.entities.Rental_ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Rental_ArticleRepository extends JpaRepository<Rental_ArticleEntity, Long> {
    // 작성자로 찾기
    Page<Rental_ArticleEntity> findByUser_UsernameIgnoreCaseContaining(String username, Pageable pageable);

    // 제목으로 찾기
    Page<Rental_ArticleEntity> findByTitleIgnoreCaseContaining(String title, Pageable pageable);


    // 공지사항
    Page<Rental_ArticleEntity> findByTag(String tag, Pageable pageable);
    Page<Rental_ArticleEntity> findByTagNot(String tag, Pageable pageable);
}
