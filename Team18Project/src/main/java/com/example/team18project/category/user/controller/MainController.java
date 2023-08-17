package com.example.team18project.category.user.controller;

import com.example.team18project.security.dto.LoginDto;
import com.example.team18project.security.dto.RegisterDto;
import com.example.team18project.category.user.service.MainService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
@RequestMapping("/main")
public class MainController {

    // DI 의존성 주입
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }
    private final MainService mainService;

    // 회원가입
    @PostMapping("/sign-up")
    @ResponseBody
    public String registerUser(@Valid @RequestBody RegisterDto dto){

        if(dto.getPassword().equals(dto.getPasswordCheck())){
            log.info("password match!");

            // 1. 일반 유저
            if (dto.getIdentityCode() == null){
                mainService.userRegister(dto);
                return "일반회원 등록완료";
            }

            // 2. 트레이너, 관장 identity_code 필요 관리자가 직접 넣어준다.
            if (dto.getIdentityCode() != null){
                mainService.gymRegister(dto);
                return "헬스트레이너 등록완료";
            }
        }
        else return "회원가입 실패";

        return "회원가입 실패";

        // 3. admin 관리자는 서버에서 직접 넣어준다 mySQL서버에서 role(string) admin
        // 4. 헬스장 관장 또한 서버에서 직접 넣어준다.
    }

    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public String generateJWT(@Valid @RequestBody LoginDto dto){

        return mainService.login(dto);
    }
}

// 0. 현재까지 진행상황 확인
// 1. GYM 정보 Identity code 는 관리자가 직접 넣어준다.
// 2. 등급제를
// 3. db 관련 얘기
// 4. 각자 맡은 카테고리 진행