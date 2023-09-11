package com.example.team18project.category.free_board.controller;


import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import com.example.team18project.category.free_board.dto.Free_Board_Comment_Dto;
import com.example.team18project.category.free_board.dto.Free_board_Post_Dto;
import com.example.team18project.category.free_board.entities.Free_ArticleEntity;
import com.example.team18project.category.free_board.entities.Free_Article_CommentEntity;
import com.example.team18project.category.free_board.entities.Free_Article_imgEntity;
import com.example.team18project.category.free_board.repos.Free_ArticleRepository;
import com.example.team18project.category.free_board.repos.Free_Article_CommentRepository;
import com.example.team18project.category.free_board.repos.Free_Article_imgRepository;
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
@RequestMapping("/community")
public class FreeBoardController {


    private final Free_ArticleRepository freeArticleRepository;
    private final UserRepository userRepository;
    private final Free_Article_imgRepository freeArticleImgRepository;
    private final Free_Article_CommentRepository freeArticleCommentRepository;


    /*@Autowired
    public FreeBoardController(
     Free_ArticleRepository freeArticleRepository,
     UserRepository userRepository,
     Free_Article_imgRepository freeArticleImgRepository,
     Free_Article_CommentRepository freeArticleCommentRepository)
 {
        this.freeArticleRepository = freeArticleRepository;
        this.userRepository = userRepository;
        this.freeArticleImgRepository = freeArticleImgRepository;
        this.freeArticleCommentRepository = freeArticleCommentRepository;


    }
*/

    @GetMapping
    public String community() {

        return "community";
    }

    //오운완 안만듦


    @GetMapping("/FreeBoard")
    public String freeBoardAll(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // "공지사항" 태그 게시물 가져오기
        Page<Free_ArticleEntity> noticeArticles = freeArticleRepository.findByTag("공지사항", pageable);

        // 나머지 게시물 가져오기 (공지사항 제외)
        Page<Free_ArticleEntity> otherArticles = freeArticleRepository.findByTagNot("공지사항", pageable);

        List<Free_ArticleEntity> allArticles = new ArrayList<>();
        allArticles.addAll(noticeArticles.getContent());
        allArticles.addAll(otherArticles.getContent());

        // 전체 게시물 리스트를 페이지네이션으로 다시 생성
        Page<Free_ArticleEntity> articlePage = new PageImpl<>(allArticles, pageable, noticeArticles.getTotalElements() + otherArticles.getTotalElements());

        model.addAttribute("articles", articlePage);

        return "FreeBoard";
    }


    @Transactional
    @PostMapping("/FreeBoard/post")
    public ResponseEntity<String> freeBoardPostStart(@RequestBody @Valid Free_board_Post_Dto dto, LocalDateTime localDateTime) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info(username);
        log.info("자유 게시판 글 작성을 시작합니다");

        Optional<UserEntity> user = userRepository.findByUsername(username);


        Free_ArticleEntity freeArticleEntity = new Free_ArticleEntity();
        freeArticleEntity.setTitle(dto.getTitle());
        freeArticleEntity.setContent(dto.getContent());
        freeArticleEntity.setTag(dto.getTag());
        freeArticleEntity.setUser(user.get());
        freeArticleEntity.setCreated_at(localDateTime.now());


        try {
            freeArticleRepository.save(freeArticleEntity);
            log.info("작성완료");

            Long generatedId = freeArticleEntity.getId();

            String Id = generatedId.toString();

            return ResponseEntity.ok(Id);
        } catch (Exception e) {
            log.error("글 작성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 작성에 실패하였습니다.");
        }


    }

    @GetMapping("/FreeBoard/post")
    public String freeBoardPost() {

        return "FreeBoardPost";
    }


    @GetMapping("/FreeBoard/search/byAuthor")
    public String freeBoardByAuthor(
            @RequestParam(name = "author") String author,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 작성자로 게시물 목록 페이지별로 가져오기
        Page<Free_ArticleEntity> articlePage = freeArticleRepository.findByUser_UsernameIgnoreCaseContaining(author, pageable);

        model.addAttribute("articles", articlePage);

        return "FreeBoard";
    }


    @GetMapping("/FreeBoard/search/byTag")
    public String freeBoardByTag(
            @RequestParam(name = "tag") String tag,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 태그로 게시물 목록 페이지별로 가져오기
        Page<Free_ArticleEntity> articlePage = freeArticleRepository.findByTagIgnoreCaseContaining(tag, pageable);

        model.addAttribute("articles", articlePage);

        return "FreeBoard";
    }


    @GetMapping("/FreeBoard/search/byTitle")
    public String freeBoardByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 태그로 게시물 목록 페이지별로 가져오기
        Page<Free_ArticleEntity> articlePage = freeArticleRepository.findByTitleIgnoreCaseContaining(title, pageable);

        model.addAttribute("articles", articlePage);

        return "FreeBoard";
    }


    // 자유 게시판 단일 조회
    @GetMapping("/FreeBoard/{id}")
    public String freeArticle(@PathVariable("id") Long id, Model model) {

        Optional<Free_ArticleEntity> freeArticle = freeArticleRepository.findById(id);
        if (freeArticle.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Free_Article_imgEntity> freeArticleImgs = freeArticleImgRepository.findAll();

        List<Free_Article_imgEntity> freeArticleImgList = new ArrayList<>();

        for (Free_Article_imgEntity Img : freeArticleImgs) {
            if (Img.getFreeArticle().getId() == id) {
                freeArticleImgList.add(Img);
            }
        }

        List<Free_Article_CommentEntity> free_comments = freeArticleCommentRepository.findAll();
        List<Free_Article_CommentEntity> article_comments = new ArrayList<>();

        for (Free_Article_CommentEntity comment : free_comments) {
            if (comment.getFreeArticle().getId() == id) {
                article_comments.add(comment);
            }
        }
        model.addAttribute("article", freeArticle.get());
        model.addAttribute("articleImgs", freeArticleImgList);
        model.addAttribute("articleComments", article_comments);
        return "FreeBoardArticle";
    }


    @PostMapping("/FreeBoard/comment/{articleId}")
    public ResponseEntity<String> postComment(@PathVariable("articleId") Long articleId,
                                              @Valid @RequestBody Free_Board_Comment_Dto requestDto,
                                              LocalDateTime time) {

        Optional<Free_ArticleEntity> freeBoardArticle = freeArticleRepository.findById(articleId);

        if (freeBoardArticle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Free_ArticleEntity free_articleentity = freeBoardArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Bad request");
        }

        // 댓글 작성 로직 (requestDto의 comment 사용)
        Free_Article_CommentEntity free_article_comment = new Free_Article_CommentEntity();
        free_article_comment.setFreeArticle(free_articleentity); // 게시글
        free_article_comment.setUser(user.get()); // 작성자
        free_article_comment.setContent(requestDto.getComment()); // 댓글내용

        LocalDateTime now = time.now();
        free_article_comment.setCreatedAt(now); // 댓글 작성 시간

        freeArticleCommentRepository.save(free_article_comment);

        return ResponseEntity.ok("Comment posted successfully");


    }
//댓글삭제
    @DeleteMapping("/FreeBoard/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 댓글 삭제 로직 수행
        Optional<Free_Article_CommentEntity> comment = freeArticleCommentRepository.findById(commentId);
        if (comment.isEmpty()){
            ResponseEntity.notFound();
        }


        if((!comment.get().getUser().getUsername().equals(username))){
            // 관리자도 아니면 badRequest
            if(!"admin".equals(username)){
                ResponseEntity.badRequest();
            }
        }
        freeArticleCommentRepository.delete(comment.get());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/FreeBoard/delete/{articleId}")
    public ResponseEntity<String> freeDelete(@PathVariable("articleId") Long articleId){
        Optional<Free_ArticleEntity> freeArticle =freeArticleRepository.findById(articleId);

        if (freeArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Free_ArticleEntity freeArticleEntity =freeArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 삭제

        //  댓글 삭제
        List<Free_Article_CommentEntity> commentList = freeArticleCommentRepository.findAll();
        for (Free_Article_CommentEntity comment: commentList) {
            if (comment.getFreeArticle().getId() == articleId){
                if(!comment.getUser().getUsername().equals(username)){
                    if(!"admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }
                freeArticleCommentRepository.delete(comment);
            }
        }


        // 이미지 삭제
        List<Free_Article_imgEntity> freeImgList = freeArticleImgRepository.findAll();

        List<Free_Article_imgEntity> articleImg = new ArrayList<>();
        for (Free_Article_imgEntity Img: freeImgList) {
            if(Img.getFreeArticle().getId() == articleId){

                if(!Img.getFreeArticle().getUser().getUsername().equals(username)){
                    if(!"admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }

                articleImg.add(Img);
            }
        }


        for (int i = 0; i < articleImg.size() ; i++) {
            String[] split = articleImg.get(i).getImgUrl().split("/");
            String name = split[split.length-1];
            String imagePath = "media/free/" + articleId + "/" + name;

            // 실제 서버에서 이미지 삭제
            try {
                Files.delete(Path.of(imagePath));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // DB에서 이미지 삭제
           freeArticleImgRepository.delete(articleImg.get(i));
        }

        // Article 삭제
        if (freeArticleEntity.getUser().getUsername().equals(username)||"admin".equals(username)){
            freeArticleRepository.delete(freeArticleEntity);
            return ResponseEntity.ok("Article deleted successfully");
        }

        return ResponseEntity.badRequest().body("bad request");
    }


    //게시판 글 수정 view
    @GetMapping("/FreeBoard/modify/{articleId}")
    public String freeBoardModifyPage(@PathVariable("articleId") Long articleId, Model model){

        Optional<Free_ArticleEntity> freeArticle = freeArticleRepository.findById(articleId);
        if (freeArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Free_Article_imgEntity> freeBoardArticleImg = freeArticleImgRepository.findAll();
        List<Free_Article_imgEntity> freeArticleImgEntityList= new ArrayList<>();

        for (Free_Article_imgEntity Img : freeBoardArticleImg ) {
            if(Img.getFreeArticle().getId() == articleId){
                freeArticleImgEntityList.add(Img);
            }
        }
        model.addAttribute("article", freeArticle.get());
        model.addAttribute("articleImgs", freeArticleImgEntityList);
        model.addAttribute("isEditMode", false);

        return "FreeBoardModify";
    }



    @PutMapping("/FreeBoard/modify/{articleId}")
    public ResponseEntity<String> freeBoardModify(@PathVariable("articleId") Long articleId,
                                                   @RequestBody Free_board_Post_Dto updateDto
    ){
        Optional<Free_ArticleEntity> freeArticle = freeArticleRepository.findById(articleId);

        if (freeArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Free_ArticleEntity freeArticleEntity =freeArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 수정 로직
        freeArticleEntity.setTag(updateDto.getTag());
        freeArticleEntity.setTitle(updateDto.getTitle());
        freeArticleEntity.setContent(updateDto.getContent());

        // 수정
        if (freeArticleEntity.getUser().getUsername().equals(username)||"admin".equals(username)){
           freeArticleRepository.save(freeArticleEntity);
            return ResponseEntity.ok("Article modify successfully");
        }
        return ResponseEntity.badRequest().body("bad request");

    }

    @DeleteMapping("/freeBoard/modify/delete/{ImagesId}")
    public ResponseEntity<String> ModifyDeleteImage(@PathVariable("ImagesId") Long ImagesId ){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("이미지 삭제 로직 실행");
        Optional<Free_Article_imgEntity> optional = freeArticleImgRepository.findById(ImagesId);
        if (optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Free_Article_imgEntity freeArticleImgEntity = optional.get();
        if (!freeArticleImgEntity.getFreeArticle().getUser().getUsername().equals(username)){
            if(!"admin".equals(username)){
                return ResponseEntity.badRequest().body("badRequest");
            }
        }

        freeArticleImgRepository.delete(freeArticleImgEntity);

        // 서버에서 이미지 삭제
        Long articleId = freeArticleImgEntity.getFreeArticle().getId();
        String[] split = freeArticleImgEntity.getImgUrl().split("/");
        String name = split[split.length-1];
        String imagePath = "media/free/" + articleId + "/" + name;

        // 실제 서버에서 이미지 삭제
        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("이미지 삭제");
    }


    @PostMapping("/FreeBoard/postImages/{id}")
    public ResponseEntity<String> freeBoardPostImage(@PathVariable String id, @RequestParam("images") MultipartFile[] images){

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
        Optional<Free_ArticleEntity> freeArticle =freeArticleRepository.findById(id);

        if(freeArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Free_ArticleEntity free_articleEntity = freeArticle.get();

        for (MultipartFile image : images){
            if (!image.isEmpty()){
                try
                {
                    // 1. 이미지 저장 경로 설정 및 폴더 생성
                    String profileDir = String.format("media/free/%d/", id);
                    Files.createDirectories(Path.of(profileDir));
                    // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
                    String originalFilename = image.getOriginalFilename();
                    // 3. 폴더와 파일 경로를 포함한 이름 만들기
                    String profilePath = profileDir + originalFilename;
                    // 4. MultipartFile 을 저장하기
                    image.transferTo(Path.of(profilePath));

                    // 5. 데이터베이스 업데이트
                    Free_Article_imgEntity ImagesEntity = new Free_Article_imgEntity();
                    ImagesEntity.setFreeArticle(free_articleEntity);
                    ImagesEntity.setImgUrl(String.format("/static/free/%d/%s", id, originalFilename));
                    freeArticleImgRepository.save(ImagesEntity);

                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image.");
                }

            }
        }
    }


}




