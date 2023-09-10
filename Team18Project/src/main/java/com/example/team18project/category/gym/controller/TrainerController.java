package com.example.team18project.category.gym.controller;

import com.example.team18project.category.gym.dto.Trainer_Post_Dto;
import com.example.team18project.category.gym.entities.*;
import com.example.team18project.category.gym.repos.*;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/gym/trainer")
public class TrainerController {

    private final TrainerLikeRepository trainerLikeRepository; // Trainer 평점
    private final TrainerImgRepository trainerImgRepository;
    private final TrainerCommentRepository trainerCommentRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;

    // 트레이너 정보
    @GetMapping("/{trainerId}")
    public String trainerInform(Model model, @PathVariable("trainerId") Long trainerId){

        Optional<Trainer_boardEntity> trainerBoard = trainerRepository.findById(trainerId);
        if (trainerBoard.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Trainer_boardEntity trainer = trainerBoard.get();
        Long gymId = trainer.getGym().getId();

        // 이미지
        List<Trainer_board_ImgEntity> trainerImgList = trainerImgRepository.findAll();
        List<Trainer_board_ImgEntity> trainerImg = new ArrayList<>();
        for (Trainer_board_ImgEntity target: trainerImgList) {
            if(target.getTrainer().getGym().getId() == gymId){
                trainerImg.add(target);
            }
        }

        // 댓글
        List<Trainer_board_CommentEntity> trainerCommentList = trainerCommentRepository.findAll();
        List<Trainer_board_CommentEntity> trainerComment = new ArrayList<>();
        for (Trainer_board_CommentEntity target: trainerCommentList) {
            if(target.getTrainer().getGym().getId() == gymId){
                trainerComment.add(target);
            }
        }

        // 평점
        List<Trainer_board_LikeEntity> trainerRateList = trainerLikeRepository.findAll();
        List<Trainer_board_LikeEntity> trainerRate = new ArrayList<>();
        for (Trainer_board_LikeEntity target: trainerRateList) {
            if(target.getTrainer().getGym().getId() == gymId){
                trainerRate.add(target);
            }
        }

        Integer rate = 0;

        for (int i = 0; i <  trainerRate.size() ; i++) {
            rate = rate + trainerRate.get(i).getRate();
        }
        // 평균 평점 (0~5)
        Integer Rate = 0;  // java.lang.ArithmeticException: / by zero 해결

        if (rate != 0) {
            Rate = rate / trainerRate.size();
        }
        else Rate = 0;
        UserEntity user = trainer.getUser();
        model.addAttribute("trainer2",user);
        model.addAttribute("trainerRate", Rate); // 트레이너 평점
        model.addAttribute("trainerComment", trainerComment); // 트레이너 댓글
        model.addAttribute("trainerImg", trainerImg); // 트레이너 사진들
        model.addAttribute("trainer", trainer); // title(트레이너이름), content(트레이너 설명)

       return "trainer";
    }

    // /gym/trainer/post
    // 오직 해당 헬스장 관장 or 트레이너만 가능
    @GetMapping("/post")
    public String trainerPostView(){
        return "trainerPost";
    }

    @Transactional
    @PostMapping("/post")
    public ResponseEntity<String> trainerPost(@RequestBody Trainer_Post_Dto dto){

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        if(user.getIdentityCode() == null){
            return ResponseEntity.badRequest().body("헬스장 소속이 아닙니다");
        }

        Optional<GymEntity> gymEntity = gymRepository.findByIdentityCode(user.getIdentityCode());
        if(gymEntity.isEmpty()){
            return ResponseEntity.badRequest().body("해당 헬스장이 없습니다");
        }

        Trainer_boardEntity trainerBoard = new Trainer_boardEntity();
        trainerBoard.setContent(dto.getContent());
        trainerBoard.setTitle(dto.getTitle());
        trainerBoard.setUser(user); // 트레이너
        trainerBoard.setGym(gymEntity.get()); // 해당 헬스장
        trainerRepository.save(trainerBoard);

        String id = trainerBoard.getId().toString();
        return ResponseEntity.ok(id);
    }

    // 이미지 넣기
    @PostMapping("/post/Images/{trainerId}")
    public ResponseEntity<String> trainerPostImage(@PathVariable("trainerId") Long trainerId, @RequestParam("images") MultipartFile[] images) {

        if(images.length == 0){
            return ResponseEntity.ok("사진이 없습니다");
        }
        try {
            saveImages(images, trainerId);
            return ResponseEntity.ok("이미지 업로드 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");
        }
    }
    public void saveImages(MultipartFile[] images, Long id){
        Optional<Trainer_boardEntity> trainerBoardEntity = trainerRepository.findById(id);

        if(trainerBoardEntity.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Trainer_boardEntity trainer =  trainerBoardEntity.get();

        for (MultipartFile image : images){
            if (!image.isEmpty()){
                try
                {
                    // 1. 이미지 저장 경로 설정 및 폴더 생성
                    String profileDir = String.format("media/trainer/%d/", id);
                    Files.createDirectories(Path.of(profileDir));
                    // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
                    String originalFilename = image.getOriginalFilename();
                    // 3. 폴더와 파일 경로를 포함한 이름 만들기
                    String profilePath = profileDir + originalFilename;
                    // 4. MultipartFile 을 저장하기
                    image.transferTo(Path.of(profilePath));

                    // 5. 데이터베이스 업데이트
                    Trainer_board_ImgEntity ImagesEntity = new  Trainer_board_ImgEntity();
                    ImagesEntity.setTrainer(trainer);
                    ImagesEntity.setImgUrl(String.format("/static/trainer/%d/%s", id, originalFilename));
                    trainerImgRepository.save(ImagesEntity);

                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패");
                }

            }
        }
    }


    // 수정 view
    @GetMapping("/modify/{trainerId}")
    public String trainerModifyView(Model model, @PathVariable("trainerId") Long trainerId){

        Optional<Trainer_boardEntity> trainerBoard = trainerRepository.findById(trainerId);
        if (trainerBoard.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 이미지
        List<Trainer_board_ImgEntity> trainerImgList = trainerImgRepository.findAll();
        List<Trainer_board_ImgEntity> trainerImg = new ArrayList<>();
        for (Trainer_board_ImgEntity target: trainerImgList ) {
            if(target.getTrainer().getId() == trainerId){
                trainerImg.add(target);
            }
        }
        Trainer_boardEntity trainer_board = trainerBoard.get();

        model.addAttribute("user",trainer_board.getUser());
        model.addAttribute("trainerImg", trainerImg);
        model.addAttribute("trainer", trainerBoard.get());
        return "trainerModify";
    }

    // 수정 동작
    @PutMapping("/modify/{trainerId}")
    public ResponseEntity<String> trainerModify(@PathVariable("trainerId") Long trainerId, @RequestBody Trainer_Post_Dto updateDto){

        Optional<Trainer_boardEntity> trainerBoard = trainerRepository.findById(trainerId);

        if (trainerBoard.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        if(user.getIdentityCode() == null){
            return ResponseEntity.badRequest().body("헬스장 소속이 아닙니다");
        }

        Trainer_boardEntity trainer_board = trainerBoard.get();
        if(!trainer_board.getGym().getIdentityCode().equals(user.getIdentityCode())){
              return ResponseEntity.badRequest().body("해당 헬스장 소속이 아닙니다");
        }

        trainer_board.setTitle(updateDto.getTitle());
        trainer_board.setContent(updateDto.getContent());
        trainerRepository.save(trainer_board);

        return ResponseEntity.ok("트레이너 정보 수정");
    }

    // 수정하면서 단일 이미지 삭제하기
    // /gym/trainer/delete/images/{ImagesId}
    @DeleteMapping("/delete/images/{ImagesId}")
    public ResponseEntity<String> ModifyDeleteImage(@PathVariable("ImagesId") Long ImagesId ) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        Optional<Trainer_board_ImgEntity> trainerBoardImg = trainerImgRepository.findById(ImagesId);
        Trainer_board_ImgEntity imgEntity = trainerBoardImg.get();

        Long trainerId = imgEntity.getTrainer().getId();

        if(trainerBoardImg.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(user.getIdentityCode() == null){
            return ResponseEntity.badRequest().body("헬스장 소속이 아닙니다");
        }
        if(!imgEntity.getTrainer().getGym().getIdentityCode().equals(user.getIdentityCode())){
            return ResponseEntity.badRequest().body("해당 헬스장 소속이 아닙니다");
        }
        trainerImgRepository.delete(imgEntity);

        // 실제 서버에서 삭제
        String[] split = imgEntity.getImgUrl().split("/");
        String name = split[split.length-1];
        String imagePath = "media/trainer/" + trainerId + "/" + name;

        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("이미지 삭제");
    }
    // 평점
    @Transactional
    @PostMapping("/grade/{trainerId}")
    public ResponseEntity<String> gymRate (@PathVariable("trainerId") Long trainerId, @RequestBody Integer rating){

        Optional<Trainer_boardEntity> Entity = trainerRepository.findById(trainerId);
        if(Entity.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Trainer_boardEntity trainer = Entity.get();

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        // 사용자가 이미 평가한 경우 업데이트하고, 아니면 새로운 평가를 생성합니다.
        Trainer_board_LikeEntity trainerRate = trainerLikeRepository.findByUserAndTrainer(user, trainer).orElse(new Trainer_board_LikeEntity());
        trainerRate.setUser(user);
        trainerRate.setTrainer(trainer);
        trainerRate.setRate(rating);

        trainerLikeRepository.save(trainerRate);
        return ResponseEntity.ok("평점부여 완료");
    }
    // 댓글
    @PostMapping("/comment/{trainerId}")
    public ResponseEntity<String> gymComment(@PathVariable("trainerId") Long trainerId, @RequestBody Map<String, String> requestBody) {

        Optional<Trainer_boardEntity> Entity = trainerRepository.findById(trainerId);
        if (Entity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Trainer_boardEntity trainer = Entity.get();

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        String content = requestBody.get("comment"); // "comment" 키의 값을 추출

        Trainer_board_CommentEntity CommentEntity = new Trainer_board_CommentEntity();
        CommentEntity.setComment(content);
        CommentEntity.setTrainer(trainer);
        CommentEntity.setUser(user);

       trainerCommentRepository.save(CommentEntity);
        return ResponseEntity.ok("댓글 작성 완료");
    }

    // 댓글 삭제
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 댓글 삭제 로직 수행
        Optional<Trainer_board_CommentEntity> comment = trainerCommentRepository.findById(commentId);
        if (comment.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if((!comment.get().getUser().getUsername().equals(username))){
            // 관리자도 아니면 badRequest
            if(!"admin".equals(username)){
                ResponseEntity.badRequest();
            }
        }
        trainerCommentRepository.delete(comment.get());

        return ResponseEntity.ok("삭제 완료");
    }

    // 전체 삭제
    @DeleteMapping("/delete/{trainerId}")
    public ResponseEntity<String> trainerDelete( @PathVariable("trainerId") Long trainerId) {

        // Trainer Img
        List<Trainer_board_ImgEntity> Trainer_Img_List = trainerImgRepository.findAll();
        for (Trainer_board_ImgEntity target: Trainer_Img_List) {
            if(target.getTrainer().getId() == trainerId){
                trainerImgRepository.delete(target);

                // 실제 서버에서 삭제
                String[] split = target.getImgUrl().split("/");
                String name = split[split.length-1];
                String imagePath = "media/trainer/" + target.getTrainer().getId() + "/" + name;

                try {
                    Files.delete(Path.of(imagePath));
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        // Trainer Comment
        List<Trainer_board_CommentEntity> Trainer_Comment_List = trainerCommentRepository.findAll();
        for (Trainer_board_CommentEntity target: Trainer_Comment_List) {
            if(target.getTrainer().getId() == trainerId){
                trainerCommentRepository.delete(target);
            }
        }

        // Trainer Likes
        List<Trainer_board_LikeEntity> Trainer_Like_List = trainerLikeRepository.findAll();
        for (Trainer_board_LikeEntity target:  Trainer_Like_List) {
            if(target.getTrainer().getId() == trainerId){
                trainerLikeRepository.delete(target);
            }
        }
        // Trainer main
        trainerRepository.deleteById(trainerId);

        return ResponseEntity.ok("전체 삭제 완료");
    }

}
