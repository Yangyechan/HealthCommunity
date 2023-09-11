package com.example.team18project.category.user.controller;

import com.example.team18project.category.user.dto.PasswordChange;
import com.example.team18project.category.user.dto.UserProfileUpdate;
import com.example.team18project.category.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("/main/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService ProfileService;

    /*
    1. 다른 유저의 프로필을 구경 할 수 있다. (프로필, 작성글, 댓글 모음) (X)
    2. 로그인 후 본인 프로필도 볼 수 있지만 이땐 @RequestParam 필요없고 유저 정보 업데이트 가능
     */


    // GET "/main/profile"
    // 현재 유저 정보 view
    @GetMapping
    public String myProfile(Model model)
    {
        return ProfileService.myProfileService(model);
    }

    // GET "/main/profile/{id}"
    // 등록된 유저 프로필 view
    @GetMapping("/{id}")
    public String profileView(
            @PathVariable("id") Long user_id,
            Model model
    ) {
        return ProfileService.profileViewService(user_id, model);
    }


    // GET "/main/profile/update"
    // MyProfile view
    @GetMapping("/update")
    public String profileUpdateView(Model model)
    {
        return ProfileService.profileUpdateViewService(model);
    }


    // PUT "/main/profile/update"
    // MyProfile Update 로직
    @PutMapping("/update")
    public ResponseEntity<String> profileUpdate(
            @RequestBody UserProfileUpdate userProfileUpdate
    ) {
        return ProfileService.profileUpdateService(userProfileUpdate);
    }


    // POST "/main/profile/image/"
    // 프로필 이미지 추가
    @PostMapping("/image")
    public ResponseEntity<String> profileImage(
            @RequestParam("image") MultipartFile image
    ) {
        return ProfileService.profileImageService(image);
    }


    // DELETE "/main/profile/image/delete"
    // 프로필 이미지 삭제
    @DeleteMapping("/image/delete")
    public ResponseEntity<String> profileImageDelete()
    {
        return ProfileService.profileImageDeleteService();
    }


    // GET "/main/profile/password/change"
    // 비밀번호 변경 view
    @GetMapping("/password/change")
    public String passwordUpdateView(Model model)
    {
        return ProfileService.passwordUpdateViewService(model);
    }


    // POST "/main/profile/password/change"
    // 비밀번호 변경
    @PostMapping("/password/change")
    public ResponseEntity<String> passwordUpdate(
            @RequestBody PasswordChange passwordChange
    ) {
        return ProfileService.passwordChangeService(passwordChange);
    }
}
