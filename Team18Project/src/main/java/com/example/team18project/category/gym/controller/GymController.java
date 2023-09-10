package com.example.team18project.category.gym.controller;

import com.example.team18project.category.gym.dto.Gym_Modify_Dto;
import com.example.team18project.category.gym.dto.Gym_Post_Dto;
import com.example.team18project.category.gym.dto.Response;
import com.example.team18project.category.gym.entities.*;
import com.example.team18project.category.gym.repos.*;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/gym")
public class GymController {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GymImgRepository gymImgRepository;
    private final GymCommentRepository gymCommentRepository;
    private final GymRateRepository gymRateRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerImgRepository trainerImgRepository;
    private final TrainerLikeRepository trainerLikeRepository;
    private final TrainerCommentRepository trainerCommentRepository;

    //http://localhost:1213/gym/main

    // 하..
    @GetMapping("/markers")
    @ResponseBody
    public List<Response> getGymsSize() {
        List<GymEntity> gyms = gymRepository.findAll();
        List<Response> List = new ArrayList<>();
        for (int i = 0; i < gyms.size() ; i++) {
            Response dto = new Response();
            dto.setId(gyms.get(i).getId().toString());
            dto.setX(String.valueOf(gyms.get(i).getX()));
            dto.setY(String.valueOf(gyms.get(i).getY()));
            dto.setTitle(gyms.get(i).getTitle());
            List.add(dto);
        }
       return List;
    }

    @GetMapping("/main")
    public String gymMain(Model model){

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();
        String startAddress = user.getAddress();

        // 현재 사용자 주소 보내기
        model.addAttribute("address", startAddress);
        // 마커용

        List<GymEntity> gymEntities = gymRepository.findAll();
        model.addAttribute("gym", gymEntities);

        return "map";
    }

    // 헬스장 정보
    @GetMapping("/{gymId}")
    public String gymInform(Model model, @PathVariable("gymId") Long gymId){

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        Optional<GymEntity> optionalGym = gymRepository.findById(gymId);

        if(optionalGym.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        GymEntity gym = optionalGym.get();

        // 헬스장 이미지
        List<Gym_imgEntity> gymImagesList = gymImgRepository.findAll();
        List<Gym_imgEntity> gymImages = new ArrayList<>();

        for (Gym_imgEntity target: gymImagesList) {
             if(target.getGym().getId() == gymId) {
                 gymImages.add(target);
             }
        }

        // 헬스장 댓글
        List<Gym_CommentEntity> gymCommentsList = gymCommentRepository.findAll();
        List<Gym_CommentEntity> gymComments = new ArrayList<>();

        for (Gym_CommentEntity target: gymCommentsList) {
            if(target.getGym().getId() == gymId){
                gymComments.add(target);
            }
        }

        // 헬스장 평점
        List<Gym_rateEntity> gymRatesList = gymRateRepository.findAll();
        List<Gym_rateEntity> gymRates = new ArrayList<>();

        for (Gym_rateEntity target : gymRatesList) {
            if(target.getGym().getId() == gymId){
                gymRates.add(target);
            }
        }

        Integer rate = 0;

        for (int i = 0; i < gymRates.size() ; i++) {
            rate = rate + gymRates.get(i).getRate();
        }
        // 평균 평점 (0~5)
        Integer gymRate = 0;  // java.lang.ArithmeticException: / by zero 해결

        if (rate != 0) {
             gymRate = rate / gymRates.size();
        }
        else  gymRate = 0;

        // 해당 트레이너 정보
        List<Trainer_boardEntity> trainerList = trainerRepository.findAll();
        List<Trainer_boardEntity> trainers = new ArrayList<>();
        for (Trainer_boardEntity target: trainerList) {
            if(target.getGym().getId() == gymId){
                trainers.add(target);
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("gym", gym);
        model.addAttribute("gymImg" ,gymImages);
        model.addAttribute("gymComment", gymComments);
        model.addAttribute("gymRate", gymRate);
        // 링크로 보여주기
        model.addAttribute("gymTrainers", trainers);

        return "gym";
    }

    // gym post view 관리자만 볼 수 있게 만든다.
    @GetMapping("/post")
    public String gymPostView(){
        return "gymPost";
    }

    // gym post 동작
    // 관리자만 해당 헬스장 post,delete 가능

    @Transactional
    @PostMapping("/post")
    public ResponseEntity<String> gymPost(@RequestBody @Valid Gym_Post_Dto dto, LocalDateTime localDateTime){

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if(!username.equals("admin")){
            return ResponseEntity.badRequest().body("권한 없음");
        }

        GymEntity gymEntity = new GymEntity();
        gymEntity.setTitle(dto.getTitle());
        gymEntity.setContent(dto.getContent());
        gymEntity.setLocation(dto.getLocation());
        gymEntity.setX(dto.getX());
        gymEntity.setY(dto.getY());

        // 헬스장 고유식별번호 생성
        UUID randomUUID = UUID.randomUUID();
        gymEntity.setIdentityCode(randomUUID.toString());
        gymRepository.save(gymEntity);

        // 관장 설정
        Optional<UserEntity> ownerEntity = userRepository.findByUsername(dto.getUsername());
        UserEntity owner = ownerEntity.get();
        owner.setRole("enema");
        owner.setIdentityCode(randomUUID.toString());
        userRepository.save(owner);

        return ResponseEntity.ok("헬스장 post 완료");
    }
    // 관장이랑 트레이너만 해당 헬스장 정보를 수정 가능하다.

    // 수정 View
    @GetMapping("/modify/{gymId}")
    public String gymModifyView (@PathVariable("gymId") Long gymId , Model model){

        Optional<GymEntity> gymEntity = gymRepository.findById(gymId);
        if(gymEntity.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 헬스장 이미지
        List<Gym_imgEntity> gymImagesList = gymImgRepository.findAll();
        List<Gym_imgEntity> gymImages = new ArrayList<>();

        for (Gym_imgEntity target: gymImagesList) {
            if(target.getGym().getId() == gymId) {
                gymImages.add(target);
            }
        }
        model.addAttribute("article", gymEntity.get());
        model.addAttribute("articleImgs", gymImages);

        return "gymModify";
    }

    // 헬스장 정보 수정 서버 동작
    @PutMapping("/modify/{gymId}")
    public ResponseEntity<String> gymModify(@PathVariable("gymId") Long gymId, @RequestBody Gym_Modify_Dto dto){

        log.info("수정을 시작합니다");
        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        Optional<GymEntity> gymEntity = gymRepository.findById(gymId);
        if(gymEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        GymEntity gym = gymEntity.get();

        // 헬스장 고유번호랑 유저의 고유번호가 다르다면
        if(!gym.getIdentityCode().equals(user.getIdentityCode())){
            // 관리자도 아니라면
            if(!user.getRole().equals("admin"))
                return ResponseEntity.badRequest().body("권한이 없습니다.");
        }

        // 수정로직
        if (dto.getLocation() != null) {
            gym.setLocation(dto.getLocation());
        }
        if (dto.getContent() != null) {
            gym.setContent(dto.getContent());
        }
        if (dto.getPhone() != null) {
            gym.setPhone(dto.getPhone());
        }
        if (dto.getTitle() != null) {
            gym.setTitle(dto.getTitle());
        }
        gymRepository.save(gym);

        return ResponseEntity.ok("수정 완료");
    }

    // 이미지 넣기
    @PostMapping("/post/Images/{gymId}")
    public ResponseEntity<String> dietInformPostImage(@PathVariable("gymId") Long Id, @RequestParam("images") MultipartFile[] images) {

        if(images.length == 0){
            return ResponseEntity.ok("사진이 없습니다");
        }
        try {
            saveImages(images, Id);
            return ResponseEntity.ok("이미지 업로드 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");
        }
    }
    public void saveImages(MultipartFile[] images, Long id){
        Optional<GymEntity> gymEntity = gymRepository.findById(id);

        if( gymEntity.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        GymEntity gym = gymEntity.get();

        for (MultipartFile image : images){
            if (!image.isEmpty()){
                try
                {
                    // 1. 이미지 저장 경로 설정 및 폴더 생성
                    String profileDir = String.format("media/gym/%d/", id);
                    Files.createDirectories(Path.of(profileDir));
                    // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
                    String originalFilename = image.getOriginalFilename();
                    // 3. 폴더와 파일 경로를 포함한 이름 만들기
                    String profilePath = profileDir + originalFilename;
                    // 4. MultipartFile 을 저장하기
                    image.transferTo(Path.of(profilePath));

                    // 5. 데이터베이스 업데이트
                    Gym_imgEntity ImagesEntity = new Gym_imgEntity();
                    ImagesEntity.setGym(gym);
                    ImagesEntity.setImg_url(String.format("/static/gym/%d/%s", id, originalFilename));
                    gymImgRepository.save(ImagesEntity);

                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패");
                }

            }
        }
    }


    // 수정하면서 헬스장 단일 이미지 삭제하기
    @DeleteMapping("/delete/images/{ImagesId}")
    public ResponseEntity<String> ModifyDeleteImage(@PathVariable("ImagesId") Long ImagesId ) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        Optional<Gym_imgEntity> gymImgEntity = gymImgRepository.findById(ImagesId);

        if(gymImgEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Long gymId = gymImgEntity.get().getGym().getId();

        Optional<GymEntity> gymEntity = gymRepository.findById(gymId);
        if(gymEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        GymEntity gym = gymEntity.get();

        // 헬스장 고유번호랑 유저의 고유번호가 다르다면
        if(!gym.getIdentityCode().equals(user.getIdentityCode())){
            // 관리자도 아니라면
            if(!user.getRole().equals("admin"))
                return ResponseEntity.badRequest().body("권한이 없습니다.");
        }

        gymImgRepository.delete(gymImgEntity.get());
        // 실제 서버에서 삭제
        String[] split =gymImgEntity.get().getImg_url().split("/");
        String name = split[split.length-1];
        String imagePath = "media/gym/" + gymId + "/" + name;

        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("이미지 삭제");
    }

    // gym 삭제 -> 관련 trainer 정보들도 다 삭제 관리자만 가능
    @DeleteMapping("/delete/{gymId}")
    public ResponseEntity<String> gymDelete( @PathVariable("gymId") Long gymId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 현재 접속자 관리자인지 확인
        if(!username.equals("admin")){
            return ResponseEntity.badRequest().body("권한이 없습니다");
        }

        Optional<GymEntity> optionalGym = gymRepository.findById(gymId);

        if(optionalGym.isEmpty()){
           return ResponseEntity.notFound().build();
        }

        GymEntity gym = optionalGym.get();

        // Trainer Delete First

        // Trainer Img
        List<Trainer_board_ImgEntity> Trainer_Img_List = trainerImgRepository.findAll();
        for (Trainer_board_ImgEntity target: Trainer_Img_List) {
            if(target.getTrainer().getGym().getId() == gymId){
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
            if(target.getTrainer().getGym().getId() == gymId){
                trainerCommentRepository.delete(target);
            }
        }

        // Trainer Likes
        List<Trainer_board_LikeEntity> Trainer_Like_List = trainerLikeRepository.findAll();
        for (Trainer_board_LikeEntity target:  Trainer_Like_List) {
            if(target.getTrainer().getGym().getId() == gymId){
                trainerLikeRepository.delete(target);
            }
        }

        // Trainer main
        List<Trainer_boardEntity> TrainerList = trainerRepository.findAll();
        for (Trainer_boardEntity target:  TrainerList) {
            if(target.getGym().getId() == gymId){
                trainerRepository.delete(target);
            }
        }

        // Gym Delete

        // Gym Img
        List<Gym_imgEntity> Gym_Img_List = gymImgRepository.findAll();
        for (Gym_imgEntity target: Gym_Img_List) {
            if(target.getGym().getId() == gymId){
                gymImgRepository.delete(target);

                // 실제 서버에서 삭제
                String[] split = target.getImg_url().split("/");
                String name = split[split.length-1];
                String imagePath = "media/gym/" + gymId + "/" + name;
                try {
                    Files.delete(Path.of(imagePath));
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        // Gym Comment
        List<Gym_CommentEntity> Gym_Comment_List = gymCommentRepository.findAll();
        for (Gym_CommentEntity target: Gym_Comment_List) {
            if(target.getGym().getId() == gymId){
               gymCommentRepository.delete(target);
            }
        }

        // Gym Rate
        List<Gym_rateEntity> Gym_Rate_List = gymRateRepository.findAll();
        for (Gym_rateEntity target: Gym_Rate_List) {
            if(target.getGym().getId() == gymId){
                gymRateRepository.delete(target);
            }
        }

        // Gym main
        gymRepository.delete(gym);


        return ResponseEntity.ok("삭제 완료");
    }

    // 평점
    @Transactional
    @PostMapping("/grade/{gymId}")
    public ResponseEntity<String> gymRate (@PathVariable("gymId") Long gymId, @RequestBody Integer rating){

        Optional<GymEntity> gymEntity = gymRepository.findById(gymId);
        if(gymEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        GymEntity gym = gymEntity.get();

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        // 사용자가 이미 평가한 경우 업데이트하고, 아니면 새로운 평가를 생성합니다.
        Gym_rateEntity gymRate = gymRateRepository.findByUserAndGym(user, gym).orElse(new Gym_rateEntity());
        gymRate.setUser(user);
        gymRate.setGym(gym);
        gymRate.setRate(rating);

        gymRateRepository.save(gymRate);
        return ResponseEntity.ok("평점부여 완료");
    }


    // 댓글 달기
    @PostMapping("/comment/{gymId}")
    public ResponseEntity<String> gymComment(@PathVariable("gymId") Long gymId, @RequestBody Map<String, String> requestBody) {

        Optional<GymEntity> gymEntity = gymRepository.findById(gymId);
        if (gymEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        GymEntity gym = gymEntity.get();

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();

        String content = requestBody.get("comment"); // "comment" 키의 값을 추출

        Gym_CommentEntity gymCommentEntity = new Gym_CommentEntity();
        gymCommentEntity.setContent(content);
        gymCommentEntity.setGym(gym);
        gymCommentEntity.setUser(user);

        gymCommentRepository.save(gymCommentEntity);
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
        Optional< Gym_CommentEntity> comment = gymCommentRepository.findById(commentId);
        if (comment.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if((!comment.get().getUser().getUsername().equals(username))){
            // 관리자도 아니면 badRequest
            if(!"admin".equals(username)){
                ResponseEntity.badRequest();
            }
        }
        gymCommentRepository.delete(comment.get());

        return ResponseEntity.ok("삭제 완료");
    }

}
