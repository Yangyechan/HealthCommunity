package com.example.team18project.category.user.controller;

import com.example.team18project.security.dto.LoginDto;
import com.example.team18project.security.dto.RegisterDto;
import com.example.team18project.category.user.service.MainService;
import com.example.team18project.security.jwt.JwtTokenUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/main")
public class MainController {

    // DI 의존성 주입
    public MainController(MainService mainService, UserDetailsManager manager, PasswordEncoder passwordEncoder
                          , JwtTokenUtils jwtTokenUtils) {
        this.mainService = mainService;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }
    private final MainService mainService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;

    // 메인페이지 view
    @GetMapping
    public String mainPage(){
        return "main";
    }

    // 회원가입 view
    @GetMapping("/sign-up")
    public String registerPage(){
        return "sign-up";
    }


    // 회원가입
    @PostMapping("/sign-up")
    @ResponseBody
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto dto){

        if(dto.getPassword().equals(dto.getPasswordCheck())){
            log.info("password match!");

            // 1. 일반 유저
            if (dto.getIdentityCode() == null){
               return mainService.userRegister(dto);
            }

            // 2. 트레이너, 관장 identity_code 필요 관리자가 직접 넣어준다.
            if (dto.getIdentityCode() != null){
                return mainService.gymRegister(dto);
            }
        }
        else  return ResponseEntity.badRequest().body("Password and passwordCheck do not match");

        return ResponseEntity.badRequest().body("Bad Request");
    }

    // 로그인 view
    @GetMapping("/login")
    public String loginPage(){

        return "login";
    }
    @PostMapping("/login")
    public ResponseEntity<String> loginPost(@Valid @RequestBody LoginDto loginForm){

        UserDetails userDetails = manager.loadUserByUsername(loginForm.getUsername());

        if(!passwordEncoder.matches(loginForm.getPassword(), userDetails.getPassword())) {
            log.info("Password incorrect"); // 잘못된 비밀번호
            return ResponseEntity.badRequest().body("Password incorrect");
        }

        String jwt = jwtTokenUtils.generateToken(userDetails);

        // 로그인 성공하면 토큰 실어서 응답하기
        return ResponseEntity.ok(jwt);

    }
    // 인증된 메인 페이지
    @GetMapping("/auth")
    public String mainAuthPage() {
        return "mainAuth";
    }

}

// 0. 현재까지 진행상황 확인
// 1. GYM 정보 Identity code 는 관리자가 직접 넣어준다.
// 2. 등급제를
// 3. db 관련 얘기
// 4. 각자 맡은 카테고리 진행