package com.example.team18project.category.user.service;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.repos.Challenge_ArticleRepository;
import com.example.team18project.category.user.dto.PasswordChange;
import com.example.team18project.category.user.dto.UserProfileUpdate;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import com.example.team18project.security.details.JpaUserDetailsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final JpaUserDetailsManager userDetailsManager;
    private final UserRepository userRepository;
    private final Challenge_ArticleRepository challengeArticleRepository;
    private final PasswordEncoder passwordEncoder;



    // 현재 유저 정보 view
    public String myProfileService(Model model) {
        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = likesCount(username);

        // 사용자 정보를 모델에 추가
        model.addAttribute("user", user);

        return "MyProfile";
    }

    // 좋아요 받은 갯수에 따른 등급 부여
    public UserEntity likesCount(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        UserEntity user = optionalUser.get();

        List<Challenge_ArticleEntity> articleList = challengeArticleRepository.findAllByUser(user);

        Integer sum = 0;

        for (Challenge_ArticleEntity article : articleList) {
            Integer cnt = article.getChallengeLikes().size();
            sum = sum + cnt;
        }

        if (sum >= 0 && sum < 3) {
            user.setGrade("blonze");
        } else if (sum >= 3 && sum < 6) {
            user.setGrade("silver");
        } else {
            user.setGrade("gold");
        }
        userRepository.save(user);
        return user;
    }

    // 등록된 유저 프로필 view
    public String profileViewService(Long user_id, Model model) {

        Optional<UserEntity> optionalUser = userRepository.findById(user_id);

        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException(optionalUser.get().getUsername());

        UserDetails userDetails
                = userDetailsManager.loadUserByUsername(optionalUser.get().getUsername());

        List<Challenge_ArticleEntity> articleList = challengeArticleRepository.findAllByUser(optionalUser.get());

        model.addAttribute("userDetails", userDetails); // Thymeleaf에 전달
        model.addAttribute("userPosts", articleList);
        return "profile"; // Thymeleaf 템플릿 파일명
    }

    // MyProfile view
    public String profileUpdateViewService(Model model) {

        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        model.addAttribute("user", optionalUser.get()); // 사용자 정보를 모델에 추가
        return "MyProfileUpdateWithKakaoAdress";
    }


    // MyProfile Update 로직
    public ResponseEntity<String> profileUpdateService(UserProfileUpdate userProfileUpdate) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = optionalUser.get();

        // 변경된 데이터가 없을 경우 업데이트를 진행하지 않음
        if (userProfileUpdate.getEmail().equals("")) {
            user.setEmail(user.getEmail());
        } else {
            user.setEmail(userProfileUpdate.getEmail());
        }

        if (userProfileUpdate.getPhone().equals("")) {
            user.setPhone(user.getPhone());
        } else {
            user.setPhone(userProfileUpdate.getPhone());
        }

        if (userProfileUpdate.getAddress().equals("")) {
            user.setAddress(user.getAddress());
        } else {
            user.setAddress(userProfileUpdate.getAddress());
        }

        userRepository.save(user);

        return ResponseEntity.ok("정보가 업데이트 되었습니다.");
    }


    // 프로필 이미지 추가
    public ResponseEntity<String> profileImageService(MultipartFile image) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        try {
            saveImages(image, username);
            return ResponseEntity.ok("Images uploaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images.");
        }
    }

    // 서버에 이미지 추가 메소드
    public void saveImages(MultipartFile image, String username) {
        Optional<UserEntity> optionalUser =userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        UserEntity user = optionalUser.get();
        if (user.getProfileImg() != null) {
            String[] fileNameSplit = user.getProfileImg().split("/");
            String fileType = fileNameSplit[fileNameSplit.length-1];
            String fileName = String.format("media/profile/%s/%s", username, fileType);
            File file = new File(fileName);
            file.delete();
        }
        try {
            // 1. 이미지 저장 경로 설정 및 폴더 생성
            String imageDir = String.format("media/profile/%s/", username);
            Files.createDirectories(Path.of(imageDir));
            // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
            String originalFilename = image.getOriginalFilename();
            // 3. 폴더와 파일 경로를 포함한 이름 만들기
            String imagePath = imageDir + originalFilename;
            // 4. MultipartFile 을 저장하기
            image.transferTo(Path.of(imagePath));

            // 5. 데이터베이스 업데이트
            user.setProfileImg(String.format("/static/profile/%s/%s", username, originalFilename));
            userRepository.save(user);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image.");
        }

    }

    // 프로필 이미지 삭제
    public ResponseEntity<String> profileImageDeleteService() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> optionalUser =userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        UserEntity user = optionalUser.get();

        try {
            String[] fileNameSplit = user.getProfileImg().split("/");
            String fileType = fileNameSplit[fileNameSplit.length-1];
            String fileName = String.format("media/profile/%s/%s", username, fileType);
            File file = new File(fileName);
            file.delete();

            user.setProfileImg(null);

            userRepository.save(user);

            return ResponseEntity.ok("프로필 이미지 삭제");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 이미지 삭제 실패: " + e.getMessage());
        }
    }


    // 비밀번호 변경 view
    public String passwordUpdateViewService(Model model) {
        // 사용자 정보 회수
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        model.addAttribute("user", optionalUser.get()); // 사용자 정보를 모델에 추가
        return "PasswordChange";
    }


    // 비밀번호 변경 로직
    public ResponseEntity<String> passwordChangeService(PasswordChange dto) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> optionalUser =userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        UserEntity user = optionalUser.get();

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {

            if (dto.getNewPassword().equals(dto.getNewPasswordCheck())) {

                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                userRepository.save(user);
                return ResponseEntity.ok("비밀번호 변경 완료");
            } else {
                return ResponseEntity.badRequest().body("새 비밀번호를 확인하세요.");
            }
        } else {
            return ResponseEntity.badRequest().body("기존 비밀번호가 일치하지 않습니다.");
        }
    }
}
