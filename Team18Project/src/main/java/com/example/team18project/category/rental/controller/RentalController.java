package com.example.team18project.category.rental.controller;

import com.example.team18project.category.rental.dto.Comment_Dto;
import com.example.team18project.category.rental.chat.jpa.ChatMessageEntity;
import com.example.team18project.category.rental.chat.jpa.ChatMessageRepository;
import com.example.team18project.category.rental.chat.jpa.ChatRoomEntity;
import com.example.team18project.category.rental.chat.jpa.ChatRoomRepository;
import com.example.team18project.category.rental.dto.Post_Dto;
import com.example.team18project.category.rental.entities.Rental_ArticleEntity;
import com.example.team18project.category.rental.entities.Rental_Article_imgEntity;
import com.example.team18project.category.rental.entities.Rental_CommentEntity;
import com.example.team18project.category.rental.repos.Rental_ArticleRepository;
import com.example.team18project.category.rental.repos.Rental_Article_imgRepository;
import com.example.team18project.category.rental.repos.Rental_CommentRepository;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
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
@RequestMapping("/rental")
public class RentalController {


    private final Rental_ArticleRepository rentalArticleRepository;
    private final Rental_CommentRepository rentalCommentRepository;
    private final Rental_Article_imgRepository rentalArticleImgRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 대여 게시판 전체 목록
    @GetMapping
    public String rental(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // "공지사항" 태그 게시물 가져오기
        Page<Rental_ArticleEntity> Articles = rentalArticleRepository.findByTag("공지사항", pageable);

        // 나머지 게시물 가져오기 (공지사항 제외)
        Page<Rental_ArticleEntity> otherArticles = rentalArticleRepository.findByTagNot("공지사항", pageable);

        List<Rental_ArticleEntity> allArticles = new ArrayList<>();
        allArticles.addAll(Articles.getContent());
        allArticles.addAll(otherArticles.getContent());

        // 전체 게시물 리스트를 페이지네이션으로 다시 생성
        Page<Rental_ArticleEntity> articlePage = new PageImpl<>(allArticles, pageable, Articles.getTotalElements() + otherArticles.getTotalElements());

        model.addAttribute("articles", articlePage);

        return "Rental";
    }

    // 대여 게시판 작성자로 찾기
    @GetMapping("/byAuthor")
    public String rentalByAuthor(
            @RequestParam(name = "author") String author,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 작성자로 게시물 목록 페이지별로 가져오기
        Page<Rental_ArticleEntity> articlePage = rentalArticleRepository.findByUser_UsernameIgnoreCaseContaining(author, pageable);

        model.addAttribute("articles", articlePage);

        return "Rental";
    }
    // 대여 게시판 제목으로 찾기

    @GetMapping("/byTitle")
    public String rentalByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 제목으로 게시물 목록 페이지별로 가져오기
        Page<Rental_ArticleEntity> articlePage = rentalArticleRepository.findByTitleIgnoreCaseContaining(title, pageable);

        model.addAttribute("articles", articlePage);

        return "Rental";
    }

    // 게시판 단일 조회

    @GetMapping("/{id}")
    public String rentalArticle(@PathVariable("id") Long id, Model model){

        Optional<Rental_ArticleEntity> InformArticle = rentalArticleRepository.findById(id);
        if (InformArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Rental_Article_imgEntity> InformArticleImgs = rentalArticleImgRepository.findAll();
        List<Rental_Article_imgEntity> InformArticleImgList = new ArrayList<>();

        for (Rental_Article_imgEntity Img : InformArticleImgs ) {
            if(Img.getRentalArticle().getId() == id){
                InformArticleImgList.add(Img);
            }
        }

        // 댓글
        List<Rental_CommentEntity> inform_comments = rentalCommentRepository.findAll();
        List<Rental_CommentEntity> article_comments = new ArrayList<>();

        for (Rental_CommentEntity comment: inform_comments) {
            if(comment.getRentalArticle().getId() == id){
                article_comments.add(comment);
            }
        }

        model.addAttribute("article", InformArticle.get());
        model.addAttribute("articleImgs", InformArticleImgList);
        model.addAttribute("articleComments", article_comments);
        return "RentalArticle";
    }

    // 댓글 로직
    @PostMapping("/comment/{articleId}")
    public ResponseEntity<String> postComment(@PathVariable("articleId") Long articleId,
                                              @Valid @RequestBody Comment_Dto requestDto,
                                              LocalDateTime time) {

        Optional<Rental_ArticleEntity> InformArticle = rentalArticleRepository.findById(articleId);

        if (InformArticle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Rental_ArticleEntity inform_article = InformArticle.get();

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
        Rental_CommentEntity inform_comment = new  Rental_CommentEntity();
        inform_comment.setRentalArticle(inform_article); // 게시글
        inform_comment.setUser(user.get()); // 작성자
        inform_comment.setContent(requestDto.getComment()); // 댓글내용

        LocalDateTime now = time.now();
        inform_comment.setCreated_at(now); // 댓글 작성 시간

        rentalCommentRepository.save(inform_comment);

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
        Optional<Rental_CommentEntity> comment = rentalCommentRepository.findById(commentId);
        if (comment.isEmpty()){
            ResponseEntity.notFound();
        }

        if((!comment.get().getUser().getUsername().equals(username))){
            // 관리자도 아니면 badRequest
            if(!"admin".equals(username)){
                ResponseEntity.badRequest();
            }
        }
        rentalCommentRepository.delete(comment.get());

        return ResponseEntity.ok().build();
    }

    // 게시판 전체 삭제

    @DeleteMapping("/delete/{articleId}")
    public ResponseEntity<String> rentalDelete(@PathVariable("articleId") Long articleId){
        Optional<Rental_ArticleEntity> InformArticle = rentalArticleRepository.findById(articleId);

        if ( InformArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Rental_ArticleEntity inform_article =  InformArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();


        // 해당 댓글 삭제
        List<Rental_CommentEntity> commentList = rentalCommentRepository.findAll();
        for (Rental_CommentEntity comment: commentList) {
            if (comment.getRentalArticle().getId() == articleId){
                if(!comment.getUser().getUsername().equals(username)){
                    if(!"admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }
                rentalCommentRepository.delete(comment);
            }
        }

        // 이미지 삭제
        List<Rental_Article_imgEntity> ImgList = rentalArticleImgRepository.findAll();

        List<Rental_Article_imgEntity> articleImg = new ArrayList<>();
        for (Rental_Article_imgEntity Img: ImgList) {
            if(Img.getRentalArticle().getId() == articleId){
                if(!Img.getRentalArticle().getUser().getUsername().equals(username)){
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
            String imagePath = "media/rental/" + articleId + "/" + name;

            // 실제 서버에서 이미지 삭제
            try {
                Files.delete(Path.of(imagePath));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // DB에서 이미지 삭제
            rentalArticleImgRepository.delete(articleImg.get(i));
        }

        // 채팅방 삭제
        Long chatRoomId = inform_article.getChatRoom().getId();
        Optional<ChatRoomEntity> chatRoom = chatRoomRepository.findById(chatRoomId);
        chatRoomRepository.delete(chatRoom.get());

        // 메세지 삭제
        List<ChatMessageEntity> chatMessages = chatMessageRepository.findByRoomId(chatRoomId);
        for (ChatMessageEntity message: chatMessages) {
            chatMessageRepository.delete(message);
        }


        // Article 삭제
        if (inform_article.getUser().getUsername().equals(username)||"admin".equals(username)){
            rentalArticleRepository.delete(inform_article);
            return ResponseEntity.ok("Article deleted successfully");
        }

        return ResponseEntity.badRequest().body("bad request");
    }

    // 게시판 글 수정 View

    @GetMapping("/modify/{articleId}")
    public String rentalModify(@PathVariable("articleId") Long articleId, Model model){

        Optional<Rental_ArticleEntity> InformArticle = rentalArticleRepository.findById(articleId);
        if ( InformArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Rental_Article_imgEntity> InformArticleImgs = rentalArticleImgRepository.findAll();
        List<Rental_Article_imgEntity> InformArticleImgList = new ArrayList<>();

        for (Rental_Article_imgEntity Img :  InformArticleImgs ) {
            if(Img.getRentalArticle().getId() == articleId){
                InformArticleImgList.add(Img);
            }
        }
        model.addAttribute("article", InformArticle.get());
        model.addAttribute("articleImgs", InformArticleImgList);
        model.addAttribute("isEditMode", false);

        return "RentalModify";
    }

    // 게시판 글 수정

    @PutMapping("/modify/{articleId}")
    public ResponseEntity<String> Modify(@PathVariable("articleId") Long articleId,
                                                   @RequestBody Post_Dto updatedArticleDto
    ){
        Optional<Rental_ArticleEntity> InformArticle = rentalArticleRepository.findById(articleId);

        if (InformArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Rental_ArticleEntity inform_article = InformArticle.get();

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
            rentalArticleRepository.save(inform_article);
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
        Optional<Rental_Article_imgEntity> optional = rentalArticleImgRepository.findById(ImagesId);
        if (optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Rental_Article_imgEntity InformArticleImg = optional.get();
        if (!InformArticleImg.getRentalArticle().getUser().getUsername().equals(username)){
            if(!"admin".equals(username)){
                return ResponseEntity.badRequest().body("badRequest");
            }
        }

        rentalArticleImgRepository.delete(InformArticleImg);

        // 서버에서 이미지 삭제
        Long articleId = InformArticleImg.getRentalArticle().getId();
        String[] split = InformArticleImg.getImg_url().split("/");
        String name = split[split.length-1];
        String imagePath = "media/rental/" + articleId + "/" + name;

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

        return "RentalPost";
    }


    // 글 작성 요청
    @PostMapping("/post")
    public ResponseEntity<String> InformPostStart(@RequestBody @Valid Post_Dto dto, LocalDateTime localDateTime){

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info(username);
        log.info("다이어트 게시판 글 작성을 시작합니다");

        Optional<UserEntity> user = userRepository.findByUsername(username);

        Rental_ArticleEntity inform_article = new Rental_ArticleEntity();
        inform_article.setTitle(dto.getTitle());
        inform_article.setContent(dto.getContent());
        inform_article.setTag(dto.getTag());
        inform_article.setUser(user.get());
        inform_article.setCreated_at(localDateTime.now());

        // 채팅방 생성
        ChatRoomEntity chatRoom = new ChatRoomEntity();
        // 제목으로 채팅방 생성
        chatRoom.setRoomName(dto.getTitle());
        chatRoomRepository.save(chatRoom);

        chatRoom.setRentalArticle(inform_article);
        inform_article.setChatRoom(chatRoom);

        try {
            rentalArticleRepository.save(inform_article);
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
    public ResponseEntity<String> InformPostImage(@PathVariable String id, @RequestParam("images") MultipartFile[] images){

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
        Optional<Rental_ArticleEntity> inform_article = rentalArticleRepository.findById(id);

        if( inform_article.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Rental_ArticleEntity InformArticle = inform_article.get();

        for (MultipartFile image : images){
            if (!image.isEmpty()){
                try
                {
                    // 1. 이미지 저장 경로 설정 및 폴더 생성
                    String profileDir = String.format("media/rental/%d/", id);
                    Files.createDirectories(Path.of(profileDir));
                    // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
                    String originalFilename = image.getOriginalFilename();
                    // 3. 폴더와 파일 경로를 포함한 이름 만들기
                    String profilePath = profileDir + originalFilename;
                    // 4. MultipartFile 을 저장하기
                    image.transferTo(Path.of(profilePath));

                    // 5. 데이터베이스 업데이트
                    Rental_Article_imgEntity ImagesEntity = new Rental_Article_imgEntity();
                    ImagesEntity.setRentalArticle(InformArticle);
                    ImagesEntity.setImg_url(String.format("/static/rental/%d/%s", id, originalFilename));
                    rentalArticleImgRepository.save(ImagesEntity);

                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image.");
                }

            }
        }
    }




}
