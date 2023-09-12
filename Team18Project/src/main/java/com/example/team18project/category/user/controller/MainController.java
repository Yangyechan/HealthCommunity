package com.example.team18project.category.user.controller;

import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import com.example.team18project.security.details.CustomUserDetails;
import com.example.team18project.security.dto.LoginDto;
import com.example.team18project.security.dto.RegisterDto;
import com.example.team18project.category.user.service.MainService;
import com.example.team18project.security.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// https 설정
// 서버에 배포하기 헬스장에 필요한 이미지들 찾기 좌표랑
// 프론트 코드 다 연결해주기
// mysql
@Slf4j
@Controller
@RequestMapping("/main")
public class MainController {

    // DI 의존성 주입
    public MainController(MainService mainService, UserDetailsManager manager, PasswordEncoder passwordEncoder
                          , JwtTokenUtils jwtTokenUtils, UserRepository userRepository) {
        this.mainService = mainService;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
    }
    private final UserRepository userRepository;
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
            log.info(dto.getIdentityCode());
            // 1. 일반 유저
            if (dto.getIdentityCode().isEmpty()){
                log.info("일반유저로 회원가입 시도");
               return mainService.userRegister(dto);
            }

            // 2. 트레이너, 관장 identity_code 필요 관리자가 직접 넣어준다.
            if (!dto.getIdentityCode().isEmpty()){
                log.info("트레이너로 회원가입 시도");
                return mainService.gymRegister(dto);
            }
        }
        else  return ResponseEntity.badRequest().body("비밀번호 확인이랑 비밀번호가 다릅니다!");

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

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAuthToken(HttpServletRequest request) {
        // 쿠키에서 토큰을 가져오기
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token != null && jwtTokenUtils.validate(token)) {
            Claims claims = jwtTokenUtils.parseClaims(token);
            long currentTime = System.currentTimeMillis() / 1000;
            long expirationTime = claims.getExpiration().getTime() / 1000;

            // 토큰 만료시간보다 15분 적으면
            if (expirationTime - currentTime <= 900) {

                String username = SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

                Optional<UserEntity> userEntity = userRepository.findByUsername(username);
                UserEntity user = userEntity.get();

                CustomUserDetails userDetails = CustomUserDetails.fromEntity(user);
                // 토큰을 재발급
                String newToken = jwtTokenUtils.generateToken(userDetails);
                return ResponseEntity.ok(newToken);
            }
        }
        return ResponseEntity.badRequest().body("토큰 재발급 필요x");
    }
}
