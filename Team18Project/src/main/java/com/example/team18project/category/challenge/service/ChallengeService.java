package com.example.team18project.category.challenge.service;

import com.example.team18project.category.challenge.dto.Challenge_ArticleDto;
import com.example.team18project.category.challenge.dto.ResponseDto;
import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_LikesEntity;
import com.example.team18project.category.challenge.repos.Challenge_ArticleRepository;
import com.example.team18project.category.challenge.repos.Challenge_LikesRepository;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final UserRepository userRepository;
    private final Challenge_ArticleRepository challengeArticleRepository;
    private final Challenge_LikesRepository challengeLikesRepository;

    /*
    코드 구상
    1. 등록된 유저만 글 작성
    2. 인증 사진을 한장만 올려도 될듯 하지만 그 한장은 반드시 올려야함.
    3. 게시글에 댓글, 좋아요 가능
    4. 좋아요도 누를수 있지만 등급에 영향은 없음.
    5. 단일 게시글 조회, 게시글들의 페이지 조회도 가능하게 해야함.
     */


    // CREATE
    public void createArticleService(Challenge_ArticleDto dto) {
        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> user = userRepository.findByUsername(username);

        if (user.isPresent()){
            Challenge_ArticleEntity newArticle = new Challenge_ArticleEntity();

            newArticle.setUser(user.get());
            newArticle.setTitle(dto.getTitle());
            newArticle.setContent(dto.getContent());

            // 현재 시간을 저장
            LocalDateTime currentTime = LocalDateTime.now();
            newArticle.setCreated_at(currentTime);

            challengeArticleRepository.save(newArticle);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    // 좋아요 기능 /자신은 좋아요를 할 수 없음, 이미 좋아요 두번 누르면 좋아요는 취소
    public ResponseDto likeArticleServie (Long article_id) {

        Optional<Challenge_ArticleEntity> optionalArticle = challengeArticleRepository.findById(article_id);

        if (optionalArticle.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        if (optionalArticle.get().getDeleted_at() != null)
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> user = userRepository.findByUsername(username);

        ResponseDto response = new ResponseDto();

        // 사용자 검증
        if (user.isPresent() && !optionalArticle.get().getUser().getUsername().equals(username)) {
            Optional<Challenge_LikesEntity> likeArticle = challengeLikesRepository.findByUserAndChallengeArticle(user.get(), optionalArticle.get());
            if (!likeArticle.isPresent()) {
                // 좋아요 새로 생성
                Challenge_LikesEntity likeArticleEntity = new Challenge_LikesEntity();
                likeArticleEntity.setUser(user.get());
                likeArticleEntity.setChallengeArticle(optionalArticle.get());
                challengeLikesRepository.save(likeArticleEntity);
                response.setResponse("좋아요");
                return response;
            } else {
                // 좋아요 취소
                challengeLikesRepository.deleteById(likeArticle.get().getId());
                response.setResponse("좋아요 취소");
                return response;
            }
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
