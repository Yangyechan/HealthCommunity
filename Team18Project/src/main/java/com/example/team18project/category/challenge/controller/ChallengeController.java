package com.example.team18project.category.challenge.controller;

import com.example.team18project.category.challenge.dto.Challenge_ArticleDto;
import com.example.team18project.category.challenge.dto.RequestDto;
import com.example.team18project.category.challenge.dto.ResponseDto;
import com.example.team18project.category.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("none/challenge")
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    // POST /articles
    // 새 게시글 생성
    @PostMapping("/articles")
    public ResponseDto create(@RequestBody Challenge_ArticleDto articleDto) {
        challengeService.createArticleService(articleDto);
        ResponseDto response = new ResponseDto();
        response.setResponse("등록이 완료되었습니다.");
        return response;
    }

    // POST /좋아요 구현
    @PostMapping("/{id}/like")
    public ResponseDto likeArticle(
            @PathVariable("id") Long article_id,
            @RequestBody RequestDto request
    ) {
        if (request.getRequest().equals("좋아요")) {
            return challengeService.likeArticleServie(article_id);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
