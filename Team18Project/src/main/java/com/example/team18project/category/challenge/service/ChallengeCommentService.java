package com.example.team18project.category.challenge.service;

import com.example.team18project.category.challenge.dto.C_CommentDto;
import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_CommentEntity;
import com.example.team18project.category.challenge.repos.Challenge_ArticleRepository;
import com.example.team18project.category.challenge.repos.Challenge_Article_imgRepository;
import com.example.team18project.category.challenge.repos.Challenge_CommentRepository;
import com.example.team18project.category.challenge.repos.Challenge_LikesRepository;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeCommentService {
    private final UserRepository userRepository;
    private final Challenge_ArticleRepository challengeArticleRepository;
    private final Challenge_LikesRepository challengeLikesRepository;
    private final Challenge_Article_imgRepository challengeArticleImgRepository;
    private final Challenge_CommentRepository challengeCommentRepository;

    // CREATE
    public ResponseEntity<String> postCommentService(Long articleId, C_CommentDto requestDto) {

        Optional<Challenge_ArticleEntity> challengeArticle = challengeArticleRepository.findById(articleId);

        if (challengeArticle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Challenge_ArticleEntity challenge_article = challengeArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            return ResponseEntity.badRequest().body("Bad request");
        }

        // 댓글 작성 로직 (requestDto의 comment 사용)
        Challenge_CommentEntity challengeComment = new Challenge_CommentEntity();
        challengeComment.setChallengeArticle(challenge_article); // 게시글
        challengeComment.setUser(user.get()); // 작성자
        challengeComment.setContent(requestDto.getComment()); // 댓글내용

        LocalDateTime currentTime = LocalDateTime.now();
        challengeComment.setCreated_at(currentTime); // 댓글 작성 시간

        challengeCommentRepository.save(challengeComment);

        return ResponseEntity.ok("Comment posted successfully");
    }

    public ResponseEntity<String> deleteCommentService(Long commentId) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 댓글 삭제 로직 수행
        Optional<Challenge_CommentEntity> comment = challengeCommentRepository.findById(commentId);
        if (comment.isEmpty()){
            ResponseEntity.notFound();
        }

        if(!comment.get().getUser().getUsername().equals(username)){
            ResponseEntity.badRequest();
        }

        challengeCommentRepository.delete(comment.get());

        return ResponseEntity.ok().build();
    }
}
