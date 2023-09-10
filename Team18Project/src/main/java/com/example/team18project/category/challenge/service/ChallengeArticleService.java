package com.example.team18project.category.challenge.service;

import com.example.team18project.category.challenge.dto.Challenge_ArticleDto;
import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_Article_imgEntity;
import com.example.team18project.category.challenge.entities.Challenge_CommentEntity;
import com.example.team18project.category.challenge.entities.Challenge_LikesEntity;
import com.example.team18project.category.challenge.repos.Challenge_ArticleRepository;
import com.example.team18project.category.challenge.repos.Challenge_Article_imgRepository;
import com.example.team18project.category.challenge.repos.Challenge_CommentRepository;
import com.example.team18project.category.challenge.repos.Challenge_LikesRepository;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeArticleService {
    private final UserRepository userRepository;
    private final Challenge_ArticleRepository challengeArticleRepository;
    private final Challenge_LikesRepository challengeLikesRepository;
    private final Challenge_Article_imgRepository challengeArticleImgRepository;
    private final Challenge_CommentRepository challengeCommentRepository;

    /*
    코드 구상
    1. 등록된 유저만 글 작성
    2. 인증 사진을 한장만 올려도 될듯 하지만 그 한장은 반드시 올려야함. (X)
    3. 게시글에 댓글, 좋아요 가능
    4. 좋아요도 누를수 있지만 등급에 영향은 없음.
    5. 단일 게시글 조회, 게시글들의 페이지 조회도 가능하게 해야함.
     */


    // Page
    public String readArticlePaged(Integer pageNumber, Model model) {
//        // 사용자 정보 회수
//        String username = SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getName();
//
//        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
//
//        if (optionalUser.isEmpty())
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        // PagingAndSortingRepository 메소드에 전달하는 용도
        // 조회하고 싶은 페이지의 정보를 담는 객체
        // 10개씩 데이터를 나눌때 0번 페이지를 달라고 요청하는 Pageable
        Pageable pageable = PageRequest.of(
                pageNumber, 10);
        // "공지사항" 태그 게시물 가져오기
        Page<Challenge_ArticleEntity> noticeArticles
                = challengeArticleRepository.findByTag("공지사항", pageable);
        // 나머지 게시물 가져오기 (공지사항 제외)
        Page<Challenge_ArticleEntity> otherArticles
                = challengeArticleRepository.findByTagNot("공지사항", pageable);

        List<Challenge_ArticleEntity> allArticles = new ArrayList<>();
        allArticles.addAll(noticeArticles.getContent());
        allArticles.addAll(otherArticles.getContent());

        // 전체 게시물 리스트를 페이지네이션으로 다시 생성
        Page<Challenge_ArticleEntity> articlePage
                = new PageImpl<>(allArticles, pageable, noticeArticles.getTotalElements() + otherArticles.getTotalElements());
        model.addAttribute("views", articlePage);
        return "ChallengeInform";
    }

    // CREATE
    public ResponseEntity<String> createArticleService(Challenge_ArticleDto dto) {
        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info(username);
        log.info("챌린지 게시판 글 작성을 시작합니다");

        Optional<UserEntity> user = userRepository.findByUsername(username);


        Challenge_ArticleEntity newArticle = new Challenge_ArticleEntity();

        newArticle.setTag(dto.getTag());
        newArticle.setUser(user.get());
        newArticle.setTitle(dto.getTitle());
        newArticle.setContent(dto.getContent());

        // 현재 시간을 저장
        LocalDateTime currentTime = LocalDateTime.now();
        newArticle.setCreated_at(currentTime);
        newArticle.setViews(0);

//        if (user.isPresent()) {
//            challengeArticleRepository.save(newArticle);
//            Long generatedId = newArticle.getId();
//            String Id = generatedId.toString();
//            return ResponseEntity.ok(Id);
//        }else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 작성에 실패하였습니다.");
//        }
        try {
            challengeArticleRepository.save(newArticle);
            Long generatedId = newArticle.getId();
            String Id = generatedId.toString();
            return ResponseEntity.ok(Id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 작성에 실패하였습니다.");
        }
    }
//            String now = currentTime.toString().replace(":", "_");
//            String[] times = now.split("\\.");
//            String time = times[0];
//            Integer cnt = 1;
//            for (MultipartFile imageFile : imageFiles) {
//                String profileDir = String.format("media/challenge/%s/%s/", username, time);
//                try {
//                    Files.createDirectories(Path.of(profileDir));
//                } catch (IOException e) {
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//
//                // 확장자를 포함한 이미지 이름 만들기 (image_{cnt}.{확장자})
//                String originalFilename = imageFile.getOriginalFilename();
//                // queue.png => fileNameSplit = {"queue", "png"}
//                String[] fileNameSplit = originalFilename.split("\\.");
//                String extension = fileNameSplit[fileNameSplit.length - 1];
//
//                String profileFilename = "image_" + String.valueOf(cnt) + "." + extension;
//
//                cnt++;
//
//                // 폴더와 파일 경로를 포함한 이름 만들기
//                String profilePath = profileDir + profileFilename;
//
//                // MultipartFile 저장하기
//                try {
//                    imageFile.transferTo(Path.of(profilePath));
//                } catch (IOException e) {
//
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//
//                // UserEntity 업데이트 (정적 프로필 이미지를 회수할 수 있는 URL)
//                // http://localhost:8080/static/username/time/image_num.png
//
//
//                Challenge_Article_imgEntity targetEntity = new Challenge_Article_imgEntity();
//                targetEntity.setChallengeArticle(newArticle);
//                targetEntity.setImg_url(String.format("/static/%s/%s/%s", username, time, profileFilename));
//                challengeArticleImgRepository.save(targetEntity);
//            }
//        }


    // 이미지 추가
    public ResponseEntity<String> challengeArticleImage(Long id, MultipartFile[] images) {

        if(images.length == 0){
            return ResponseEntity.ok("No Images");
        }
        try {
            saveImages(images, id);
            return ResponseEntity.ok("Images uploaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images.");
        }
    }

    // 서버 이미지 추가 로직
    public void saveImages(MultipartFile[] images, Long id) {
        Optional<Challenge_ArticleEntity> challenge_article = challengeArticleRepository.findById(id);

        if (challenge_article.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Challenge_ArticleEntity challengeArticle = challenge_article.get();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                try {
                    // 1. 이미지 저장 경로 설정 및 폴더 생성
                    String imageDir = String.format("media/challenge/%d/", id);
                    Files.createDirectories(Path.of(imageDir));
                    // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
                    String originalFilename = image.getOriginalFilename();
                    // 3. 폴더와 파일 경로를 포함한 이름 만들기
                    String imagePath = imageDir + originalFilename;
                    // 4. MultipartFile 을 저장하기
                    image.transferTo(Path.of(imagePath));

                    // 5. 데이터베이스 업데이트
                    Challenge_Article_imgEntity ImagesEntity = new Challenge_Article_imgEntity();
                    ImagesEntity.setChallengeArticle(challengeArticle);
                    ImagesEntity.setImg_url(String.format("/static/challenge/%d/%s", id, originalFilename));
                    challengeArticleImgRepository.save(ImagesEntity);

                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image.");
                }

            }
        }
    }

    // 단일 게시글 조회
    public String readArticle (Long article_id, Model model) {
        Optional<Challenge_ArticleEntity> optionalArticle = challengeArticleRepository.findById(article_id);

        if (optionalArticle.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            // 엔티티에서 조회수 증가 처리
            Challenge_ArticleEntity article = optionalArticle.get();
            article.setViews(article.getViews() + 1); // 조회수 증가
            challengeArticleRepository.save(article); // 엔티티 업데이트

            // 이미지
            List<Challenge_Article_imgEntity> challengeArticleImgs
                    = challengeArticleImgRepository.findAllByChallengeArticle(optionalArticle.get());


            List<Challenge_CommentEntity> challengeComments
                    = challengeCommentRepository.findAllByChallengeArticle(optionalArticle.get());

            List<Challenge_LikesEntity> challengeLikes = optionalArticle.get().getChallengeLikes();



            model.addAttribute("like", challengeLikes.size());
            model.addAttribute("article", optionalArticle.get());
            model.addAttribute("articleImgs", challengeArticleImgs);
            model.addAttribute("articleComments", challengeComments);
            return "ChallengeInformArticle";
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    // 좋아요 기능 /자신은 좋아요를 할 수 없음, 이미 좋아요 두번 누르면 좋아요는 취소
    public ResponseEntity<String> likeArticleServie (Long article_id) {

        Optional<Challenge_ArticleEntity> optionalArticle = challengeArticleRepository.findById(article_id);

        if (optionalArticle.isEmpty())
            return ResponseEntity.notFound().build();
//        if (optionalArticle.get().getDeleted_at() != null)
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> user = userRepository.findByUsername(username);



        // 사용자 검증
        if (user.isPresent() && !optionalArticle.get().getUser().getUsername().equals(username)) {
            Optional<Challenge_LikesEntity> likeArticle
                    = challengeLikesRepository.findByUserAndChallengeArticle(user.get(), optionalArticle.get());
            if (!likeArticle.isPresent()) {
                // 좋아요 새로 생성
                Challenge_LikesEntity likeArticleEntity = new Challenge_LikesEntity();
                likeArticleEntity.setUser(user.get());
                likeArticleEntity.setChallengeArticle(optionalArticle.get());
                challengeLikesRepository.save(likeArticleEntity);

                return ResponseEntity.ok("좋아요!");
            } else {
                // 좋아요 취소
                challengeLikesRepository.deleteById(likeArticle.get().getId());

                return ResponseEntity.ok("좋아요 삭제!");
            }
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE 게시글, 게시글 이미지 삭제
    public ResponseEntity<String> deleteArticleService(Long article_id) {
        Optional<Challenge_ArticleEntity> optionalArticle = challengeArticleRepository.findById(article_id);

        if (optionalArticle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (optionalArticle.get().getUser().getUsername().equals(username)) {
            List<Challenge_Article_imgEntity> imageEntities = optionalArticle.get().getChallengeArticleImgs();

            for (Challenge_Article_imgEntity imageEntity : imageEntities) {
                // 서버에 저장되어있는 사진 삭제
                String img_url = imageEntity.getImg_url();
                String[] fileNameSplit = img_url.split("/");
                String fileType = fileNameSplit[fileNameSplit.length - 1];
                String fileName = String.format("media/challenge/%d/%s", article_id, fileType);

                File file = new File(fileName);
                file.delete();
            }

            challengeArticleRepository.deleteById(article_id);
            return ResponseEntity.ok("Article deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("bad request");
        }
    }

    // Page
    // 페이지 제목 키워드 검색
    public String readSearchArticlePage(Integer pageNumber, String title, Model model)
    {
        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        // PagingAndSortingRepository 메소드에 전달하는 용도
        // 조회하고 싶은 페이지의 정보를 담는 객체
        // 10개씩 데이터를 나눌때 0번 페이지를 달라고 요청하는 Pageable
        Pageable pageable = PageRequest.of(
                pageNumber, 10);
        Page<Challenge_ArticleEntity> articlePage
                = challengeArticleRepository.findAllByTitleContaining(title, pageable);

        model.addAttribute("views", articlePage);
        return "ChallengeInform";
    }

    // 챌린지 게시판 글 수정 view
    public String challengeModifyPage(Long articleId, Model model){

        Optional<Challenge_ArticleEntity> challengeArticle = challengeArticleRepository.findById(articleId);
        if (challengeArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
//        List<Challenge_Article_imgEntity> challengeArticleImgs = challengeArticleImgRepository.findAll();
//        List<Challenge_Article_imgEntity> challengeArticleImgList = new ArrayList<>();
//
//        for (Challenge_Article_imgEntity Img : challengeArticleImgs ) {
//            if(Img.getChallengeArticle().getId() == articleId){
//                challengeArticleImgList.add(Img);
//            }
//        }

        List<Challenge_Article_imgEntity> challengeArticleImgList
                = challengeArticleImgRepository.findAllByChallengeArticle(challengeArticle.get());

        model.addAttribute("article", challengeArticle.get());
        model.addAttribute("articleImgs", challengeArticleImgList);
        model.addAttribute("isEditMode", false);

        return "ChallengeModify";
    }

    // 챌린지 게시판 글 수정
    public ResponseEntity<String> challengeModify(Long articleId, Challenge_ArticleDto updatedArticleDto) {

        Optional<Challenge_ArticleEntity> challengeArticle = challengeArticleRepository.findById(articleId);

        if (challengeArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Challenge_ArticleEntity challenge_article = challengeArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 수정 로직
        challenge_article.setTag(updatedArticleDto.getTag());
        challenge_article.setTitle(updatedArticleDto.getTitle());
        challenge_article.setContent(updatedArticleDto.getContent());

        // 수정
        if (challenge_article.getUser().getUsername().equals(username)||"admin".equals(username)){
            challengeArticleRepository.save(challenge_article);
            return ResponseEntity.ok("Article modify successfully");
        }
        return ResponseEntity.badRequest().body("bad request");
    }

    // 게시글 수정하면서 이미지 삭제
    public ResponseEntity<String> ModifyDeleteImage(Long imageId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<Challenge_Article_imgEntity> optional = challengeArticleImgRepository.findById(imageId);
        if (optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Challenge_Article_imgEntity challengeArticleImg = optional.get();
        if (!challengeArticleImg.getChallengeArticle().getUser().getUsername().equals(username)){
            if(!"admin".equals(username)){
                return ResponseEntity.badRequest().body("badRequest");
            }
        }

        challengeArticleImgRepository.delete(challengeArticleImg);

        // 서버에서 이미지 삭제
        Long articleId = challengeArticleImg.getChallengeArticle().getId();
        String[] split = challengeArticleImg.getImg_url().split("/");
        String name = split[split.length-1];
        String imagePath = "media/challenge/" + articleId + "/" + name;

        // 실제 서버에서 이미지 삭제
        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("이미지 삭제");
    }
}
