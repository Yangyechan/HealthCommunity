package com.example.team18project.category.challenge.controller;

import com.example.team18project.category.challenge.dto.C_CommentDto;
import com.example.team18project.category.challenge.dto.Challenge_ArticleDto;
import com.example.team18project.category.challenge.service.ChallengeArticleService;
import com.example.team18project.category.challenge.service.ChallengeCommentService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/challenge")
@RequiredArgsConstructor
public class ChallengeArticleController {
    private final ChallengeArticleService articleService;
    private final ChallengeCommentService commentService;

    // GET Pages "/challenge/pages"
    // 전체 게시글 조회
    @GetMapping("/pages")
    public String readAll(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            Model model
    ) {
        return articleService.readArticlePaged(pageNumber, model);
    }

    // GET "/challenge/write"
    // 챌린지 게시판 글 작성 view
    @GetMapping("/write")
    public String challengePost(){
        return "ChallengeInformPost";
    }

    // POST "/challenge/write"
    // 새 게시글 생성
    @PostMapping("/write")
    public ResponseEntity<String> createChallenge(
            @RequestBody @Valid Challenge_ArticleDto articleDto
    ) {
        return articleService.createArticleService(articleDto);
    }

    // POST "/challenge/postImages/{id}"
    // 이미지 업로드
    @PostMapping("/postImages/{id}")
    public ResponseEntity<String> challengePostImage(
            @PathVariable Long id,
            @RequestParam("images") MultipartFile[] images
    ){
        return articleService.challengeArticleImage(id, images);
    }

    // GET Pages "/challenge/pages/{id}"
    // 단일 조회
    @GetMapping("/pages/{id}")
    public String readArticleByArticleId(
            @PathVariable("id") Long article_id,
            Model model
    ) {
        return articleService.readArticle(article_id, model);
    }

    // POST "/challenge/like/{id}"
    // 좋아요 구현
    @Transactional
    @PostMapping("/like/{id}")
    public ResponseEntity<String> likeArticle(
            @PathVariable("id") Long article_id
    ) {
        return articleService.likeArticleServie(article_id);
    }

    // DELETE "/challenge/delete/{id}"
    // 게시글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteArticle(
            @PathVariable("id") Long article_id
    ) {
        return articleService.deleteArticleService(article_id);
    }

    // GET "/challenge/pages/search/byTitle"
    // 제목으로 게시글 검색
    @GetMapping("/pages/search/byTitle")
    public String readSearch(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "title") String title,
            Model model
    ) {
        return articleService.readSearchArticlePage(pageNumber, title, model);
    }

    // GET "/challenge/modify/{articleId}"
    // 게시글 수정 view
    @GetMapping("/modify/{articleId}")
    public String dietInformModify(
            @PathVariable("articleId") Long articleId,
            Model model
    ){
        return articleService.challengeModifyPage(articleId, model);
    }

    // PUT "/challenge/modify/{articleId}"
    // 게시판 글 수정
    @PutMapping("/modify/{articleId}")
    public ResponseEntity<String> ChallengeArticleModify(
            @PathVariable("articleId") Long articleId,
            @RequestBody Challenge_ArticleDto updatedArticleDto
    ) {
        return articleService.challengeModify(articleId, updatedArticleDto);
    }

    // DELETE "/challenge/modify/delete/{ImagesId}"
    // 게시글 수정하면서 단일 이미지 삭제
    @DeleteMapping("/modify/delete/{ImagesId}")
    public ResponseEntity<String> ModifyDeleteImage(
            @PathVariable("ImagesId") Long imageId
    ) {
        return articleService.ModifyDeleteImage(imageId);
    }

    // POST "/challenge/comment/{articleId}"
    // 댓글 작성
    @PostMapping("/comment/{articleId}")
    public ResponseEntity<String> postComment(
            @PathVariable("articleId") Long articleId,
            @Valid @RequestBody C_CommentDto requestDto
    ) {
        return commentService.postCommentService(articleId, requestDto);
    }

    // DELETE "/challenge/comment/delete/{commentId}"
    // 댓글 삭제 (댓글은 수정 불가능)
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable("commentId") Long commentId
    ) {
        return commentService.deleteCommentService(commentId);
    }
}
