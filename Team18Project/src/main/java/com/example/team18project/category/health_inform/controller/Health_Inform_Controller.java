package com.example.team18project.category.health_inform.controller;

import com.example.team18project.category.health_inform.dto.Comment_Dto;
import com.example.team18project.category.health_inform.dto.Post_Dto;
import com.example.team18project.category.health_inform.entities.Health_Inform_ArticleEntity;
import com.example.team18project.category.health_inform.entities.Health_Inform_Article_imgEntity;
import com.example.team18project.category.health_inform.entities.Health_Inform_CommentEntity;
import com.example.team18project.category.health_inform.entities.Health_Inform_LikesEntity;
import com.example.team18project.category.health_inform.repos.Health_Inform_ArticleRepository;
import com.example.team18project.category.health_inform.repos.Health_Inform_CommentRepository;
import com.example.team18project.category.health_inform.repos.Health_inform_Article_imgRepository;
import com.example.team18project.category.health_inform.repos.Health_inform_LikesRepository;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/healthInform")
public class Health_Inform_Controller {


    private final Health_Inform_ArticleRepository healthInformArticleRepository;
    private final Health_inform_Article_imgRepository healthInformArticleImgRepository;
    private final Health_Inform_CommentRepository healthInformCommentRepository;
    private final Health_inform_LikesRepository healthInformLikesRepository;
    private final UserRepository userRepository;


    // 게시판 전체 목록 태그 공지사항 순서대로
    @GetMapping
    public String Inform(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // "공지사항" 태그 게시물 가져오기
        Page<Health_Inform_ArticleEntity> noticeArticles = healthInformArticleRepository.findByTag("공지사항", pageable);

        // 나머지 게시물 가져오기 (공지사항 제외)
        Page<Health_Inform_ArticleEntity> otherArticles = healthInformArticleRepository.findByTagNot("공지사항", pageable);

        List<Health_Inform_ArticleEntity> allArticles = new ArrayList<>();
        allArticles.addAll(noticeArticles.getContent());
        allArticles.addAll(otherArticles.getContent());

        // 전체 게시물 리스트를 페이지네이션으로 다시 생성
        Page<Health_Inform_ArticleEntity> articlePage = new PageImpl<>(allArticles, pageable, noticeArticles.getTotalElements() + otherArticles.getTotalElements());

        model.addAttribute("articles", articlePage);

        return "HealthInform";
    }

    // 게시판 작성자로 찾기
    @GetMapping("/byAuthor")
    public String InformByAuthor(
            @RequestParam(name = "author") String author,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 작성자로 게시물 목록 페이지별로 가져오기
        Page<Health_Inform_ArticleEntity> articlePage =  healthInformArticleRepository.findByUser_UsernameIgnoreCaseContaining(author, pageable);

        model.addAttribute("articles", articlePage);

        return "HealthInform";
    }

    // 게시판 태그로 찾기
    @GetMapping("/byTag")
    public String InformByTag(
            @RequestParam(name = "tag") String tag,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 태그로 게시물 목록 페이지별로 가져오기
        Page<Health_Inform_ArticleEntity> articlePage =  healthInformArticleRepository.findByTagIgnoreCaseContaining(tag, pageable);

        model.addAttribute("articles", articlePage);

        return "HealthInform";
    }

    // 게시판 제목으로 찾기
    @GetMapping("/byTitle")
    public String InformByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 제목으로 게시물 목록 페이지별로 가져오기
        Page<Health_Inform_ArticleEntity> articlePage = healthInformArticleRepository.findByTitleIgnoreCaseContaining(title, pageable);

        model.addAttribute("articles", articlePage);

        return "HealthInform";
    }

    // 게시판 단일 조회
    @GetMapping("/{id}")
    public String InformArticle(@PathVariable("id") Long id, Model model){

        Optional<Health_Inform_ArticleEntity> InformArticle = healthInformArticleRepository.findById(id);
        if (InformArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Health_Inform_Article_imgEntity> InformArticleImgs = healthInformArticleImgRepository.findAll();
        List<Health_Inform_Article_imgEntity> InformArticleImgList = new ArrayList<>();

        for (Health_Inform_Article_imgEntity Img : InformArticleImgs ) {
            if(Img.getHealthInformArticle().getId() == id){
                InformArticleImgList.add(Img);
            }
        }

        // 댓글
        List<Health_Inform_CommentEntity> inform_comments = healthInformCommentRepository.findAll();
        List<Health_Inform_CommentEntity> article_comments = new ArrayList<>();

        for (Health_Inform_CommentEntity comment: inform_comments) {
            if(comment.getHealthInformArticle().getId() == id){
                article_comments.add(comment);
            }
        }
        // 좋아요
        List<Health_Inform_LikesEntity> InformLikes = healthInformLikesRepository.findAll();
        int like = 0;

        for (Health_Inform_LikesEntity likes: InformLikes ) {
            if(likes.getHealthInformArticle().getId() == id){
                like ++;
            }
        }

        model.addAttribute("like", like);
        model.addAttribute("article", InformArticle.get());
        model.addAttribute("articleImgs", InformArticleImgList);
        model.addAttribute("articleComments", article_comments);
        return "HealthInformArticle";
    }

    // 좋아요 로직

    @Transactional
    @PostMapping("/like/{articleId}")
    public ResponseEntity<String> postLike(@PathVariable("articleId") Long articleId){

        log.info("좋아요 로직 실행");

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 좋아요 하려고 하는 피드가 있는지 확인
        Optional<Health_Inform_ArticleEntity> inform_article = healthInformArticleRepository.findById(articleId);

        if(inform_article.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Health_Inform_ArticleEntity InformArticle = inform_article.get();

        // 좋아요 하려고 하는 피드가 자신이 작성한 피드인지 확인
        if(InformArticle.getUser().getUsername().equals(username)){
            return ResponseEntity.badRequest().body("자신이 작성한 피드에는 좋아요는 못 누른다");
        }

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();
        Long userId = user.getId();

        Optional<Health_Inform_LikesEntity> likesOptional = healthInformLikesRepository.findByUserIdAndHealthInformArticleId(userId, articleId);
        // 해당 피드에 현재 사용자가 좋아요를 누른적이 없다면 좋아요

        if(likesOptional.isEmpty()){
            Health_Inform_LikesEntity InformLikes = new Health_Inform_LikesEntity();
            InformLikes.setHealthInformArticle(InformArticle);
            InformLikes.setUser(user);
            healthInformLikesRepository.save(InformLikes);
            log.info("좋아요");
            return ResponseEntity.ok("좋아요!");
        }
        // 이미 눌렀으면 삭제
        else{
            healthInformLikesRepository.deleteByHealthInformArticleIdAndUserId(articleId, userId);
            log.info("좋아요 삭제");
            return ResponseEntity.ok("좋아요 삭제!");
        }

    }

    // 댓글 로직
    @PostMapping("/comment/{articleId}")
    public ResponseEntity<String> postComment(@PathVariable("articleId") Long articleId,
                                              @Valid @RequestBody Comment_Dto requestDto,
                                              LocalDateTime time) {

        Optional<Health_Inform_ArticleEntity> InformArticle = healthInformArticleRepository.findById(articleId);

        if (InformArticle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Health_Inform_ArticleEntity inform_article = InformArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            return ResponseEntity.badRequest().body("Bad request");
        }

        // 댓글 작성 로직
        Health_Inform_CommentEntity inform_comment = new  Health_Inform_CommentEntity();
        inform_comment.setHealthInformArticle(inform_article); // 게시글
        inform_comment.setUser(user.get()); // 작성자
        inform_comment.setContent(requestDto.getComment()); // 댓글내용

        LocalDateTime now = time.now();
        inform_comment.setCreated_at(now); // 댓글 작성 시간

        healthInformCommentRepository.save(inform_comment);

        return ResponseEntity.ok("Comment posted successfully");
    }

    // 댓글 삭제(댓글 수정은 불가능)

    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 댓글 삭제 로직 수행
        Optional<Health_Inform_CommentEntity> comment = healthInformCommentRepository.findById(commentId);
        if (comment.isEmpty()){
            ResponseEntity.notFound();
        }


        if((!comment.get().getUser().getUsername().equals(username))){
            // 관리자도 아니면 badRequest
            if(!"admin".equals(username)){
                ResponseEntity.badRequest();
            }
        }
        healthInformCommentRepository.delete(comment.get());

        return ResponseEntity.ok().build();
    }

    // 게시판 전체 삭제

    @DeleteMapping("/delete/{articleId}")
    public ResponseEntity<String> dietInformDelete(@PathVariable("articleId") Long articleId){
        Optional<Health_Inform_ArticleEntity> InformArticle = healthInformArticleRepository.findById(articleId);

        if ( InformArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Health_Inform_ArticleEntity inform_article =  InformArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();


        // 해당 댓글 삭제
        List<Health_Inform_CommentEntity> commentList = healthInformCommentRepository.findAll();
        for (Health_Inform_CommentEntity comment: commentList) {
            if (comment.getHealthInformArticle().getId() == articleId){
                if(!comment.getUser().getUsername().equals(username)){
                    if(!"admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }
                healthInformCommentRepository.delete(comment);
            }
        }

        // 해당 좋아요 삭제
        List<Health_Inform_LikesEntity> InformLikes = healthInformLikesRepository.findAll();

        for (Health_Inform_LikesEntity likes: InformLikes ) {
            if(likes.getHealthInformArticle().getId() == articleId){
                if(!likes.getUser().getUsername().equals(username)){
                    if("admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }

                healthInformLikesRepository.delete(likes);
            }
        }

        // 이미지 삭제
        List<Health_Inform_Article_imgEntity> ImgList = healthInformArticleImgRepository.findAll();

        List<Health_Inform_Article_imgEntity> articleImg = new ArrayList<>();
        for (Health_Inform_Article_imgEntity Img: ImgList) {
            if(Img.getHealthInformArticle().getId() == articleId){
                if(!Img.getHealthInformArticle().getUser().getUsername().equals(username)){
                    if(!"admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }
                articleImg.add(Img);
            }
        }


        for (int i = 0; i < articleImg.size() ; i++) {
            String[] split = articleImg.get(i).getImg_url().split("/");
            String name = split[split.length-1];
            String imagePath = "media/diet/" + articleId + "/" + name;

            // 실제 서버에서 이미지 삭제
            try {
                Files.delete(Path.of(imagePath));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // DB에서 이미지 삭제
            healthInformArticleImgRepository.delete(articleImg.get(i));
        }

        // Article 삭제
        if (inform_article.getUser().getUsername().equals(username)||"admin".equals(username)){
            healthInformArticleRepository.delete(inform_article);
            return ResponseEntity.ok("Article deleted successfully");
        }

        return ResponseEntity.badRequest().body("bad request");
    }

    // 게시판 글 수정 View

    @GetMapping("/modify/{articleId}")
    public String dietInformModifyPage(@PathVariable("articleId") Long articleId, Model model){

        Optional<Health_Inform_ArticleEntity> InformArticle = healthInformArticleRepository.findById(articleId);
        if ( InformArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Health_Inform_Article_imgEntity> InformArticleImgs = healthInformArticleImgRepository.findAll();
        List<Health_Inform_Article_imgEntity> InformArticleImgList = new ArrayList<>();

        for (Health_Inform_Article_imgEntity Img :  InformArticleImgs ) {
            if(Img.getHealthInformArticle().getId() == articleId){
                InformArticleImgList.add(Img);
            }
        }
        model.addAttribute("article", InformArticle.get());
        model.addAttribute("articleImgs", InformArticleImgList);
        model.addAttribute("isEditMode", false);

        return "InformModify";
    }

    // 게시판 글 수정 요청

    @PutMapping("/modify/{articleId}")
    public ResponseEntity<String> dietInformModify(@PathVariable("articleId") Long articleId,
                                                   @RequestBody Post_Dto updatedArticleDto
    )
    {
        Optional<Health_Inform_ArticleEntity> InformArticle = healthInformArticleRepository.findById(articleId);

        if ( InformArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Health_Inform_ArticleEntity inform_article = InformArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 수정 로직
        inform_article.setTag(updatedArticleDto.getTag());
        inform_article.setTitle(updatedArticleDto.getTitle());
        inform_article.setContent(updatedArticleDto.getContent());

        // 수정
        if (inform_article.getUser().getUsername().equals(username)||"admin".equals(username)){
            healthInformArticleRepository.save(inform_article);
            return ResponseEntity.ok("Article modify successfully");
        }
        return ResponseEntity.badRequest().body("bad request");
    }

    // 게시판 수정하면서 단일 이미지 삭제

    @DeleteMapping("/modify/delete/{ImagesId}")
    public ResponseEntity<String> ModifyDeleteImage(@PathVariable("ImagesId") Long ImagesId ){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("이미지 삭제 로직 실행");
        Optional<Health_Inform_Article_imgEntity> optional = healthInformArticleImgRepository.findById(ImagesId);
        if (optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Health_Inform_Article_imgEntity InformArticleImg = optional.get();
        if (!InformArticleImg.getHealthInformArticle().getUser().getUsername().equals(username)){
            if(!"admin".equals(username)){
                return ResponseEntity.badRequest().body("badRequest");
            }
        }

        healthInformArticleImgRepository.delete(InformArticleImg);

        // 서버에서 이미지 삭제
        Long articleId = InformArticleImg.getHealthInformArticle().getId();
        String[] split = InformArticleImg.getImg_url().split("/");
        String name = split[split.length-1];
        String imagePath = "media/diet/" + articleId + "/" + name;

        // 실제 서버에서 이미지 삭제
        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("이미지 삭제");
    }

    // 글 작성 view
    @GetMapping("/post")
    public String InformPost(){

        return "InformPost";
    }

    // 글 작성 요청
    @PostMapping("/post")
    public ResponseEntity<String> dietInformPostStart(@RequestBody @Valid Post_Dto dto, LocalDateTime localDateTime){

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info(username);
        log.info("다이어트 게시판 글 작성을 시작합니다");

        Optional<UserEntity> user = userRepository.findByUsername(username);

        Health_Inform_ArticleEntity inform_article = new Health_Inform_ArticleEntity();
        inform_article.setTitle(dto.getTitle());
        inform_article.setContent(dto.getContent());
        inform_article.setTag(dto.getTag());
        inform_article.setUser(user.get());
        inform_article.setCreated_at(localDateTime.now());

        try {
            healthInformArticleRepository.save(inform_article);
            log.info("작성완료");

            Long generatedId =  inform_article.getId();

            String Id = generatedId.toString();

            return ResponseEntity.ok(Id);
        } catch (Exception e) {
            log.error("글 작성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 작성에 실패하였습니다.");
        }
    }

    // 이미지 업로드

    @PostMapping("/postImages/{id}")
    public ResponseEntity<String> dietInformPostImage(@PathVariable String id, @RequestParam("images") MultipartFile[] images){

        Long Id = Long.parseLong(id);

        if(images.length == 0){
            return ResponseEntity.ok("No Images");
        }
        try {
            saveImages(images, Id);
            return ResponseEntity.ok("Images uploaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images.");
        }
    }
    public void saveImages(MultipartFile[] images, Long id){
        Optional<Health_Inform_ArticleEntity> inform_article = healthInformArticleRepository.findById(id);

        if( inform_article.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Health_Inform_ArticleEntity InformArticle = inform_article.get();

        for (MultipartFile image : images){
            if (!image.isEmpty()){
                try
                {
                    // 1. 이미지 저장 경로 설정 및 폴더 생성
                    String profileDir = String.format("media/diet/%d/", id);
                    Files.createDirectories(Path.of(profileDir));
                    // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
                    String originalFilename = image.getOriginalFilename();
                    // 3. 폴더와 파일 경로를 포함한 이름 만들기
                    String profilePath = profileDir + originalFilename;
                    // 4. MultipartFile 을 저장하기
                    image.transferTo(Path.of(profilePath));

                    // 5. 데이터베이스 업데이트
                    Health_Inform_Article_imgEntity ImagesEntity = new   Health_Inform_Article_imgEntity();
                    ImagesEntity.setHealthInformArticle(InformArticle);
                    ImagesEntity.setImg_url(String.format("/static/diet/%d/%s", id, originalFilename));
                    healthInformArticleImgRepository.save(ImagesEntity);

                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image.");
                }

            }
        }
    }




}
