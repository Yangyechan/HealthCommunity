package com.example.team18project.category.user.service;

import com.example.team18project.category.gym.entities.GymEntity;
import com.example.team18project.category.gym.repos.GymRepository;
import com.example.team18project.category.user.entities.UserEntity;
import com.example.team18project.category.user.repos.UserRepository;
import com.example.team18project.security.details.CustomUserDetails;
import com.example.team18project.security.details.JpaUserDetailsManager;
import com.example.team18project.security.dto.LoginDto;
import com.example.team18project.security.dto.RegisterDto;
import com.example.team18project.security.jwt.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Slf4j
@Service
public class MainService {

    // DI 의존성 주입
    public MainService(
            GymRepository gymRepository,
            UserRepository userRepository,
            UserDetailsManager manager,
            JpaUserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder,
            JwtTokenUtils jwtTokenUtils) {

        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.manager = manager;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    private final UserRepository userRepository;
    private final UserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;
    private final GymRepository gymRepository;
    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsManager userDetailsManager;

    // 일반 회원 등록
    public ResponseEntity<String> userRegister(RegisterDto dto){

        if(userDetailsManager.userExists(dto.getUsername())){
            return ResponseEntity.badRequest().body("User already exists");
        }

        manager.createUser(CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .birth(dto.getBirth())
                .gender(dto.getGender())
                .grade("default") // 등급제 어떻게 해야할지
                .border("black")
                .role("일반회원")
                .build()
        );

        return ResponseEntity.ok("일반회원 registration successful");
    }

    // 헬스장 트레이너 등록
    public ResponseEntity gymRegister(RegisterDto dto){

        String identity_code = dto.getIdentityCode();

        Optional<GymEntity> gymEntity = gymRepository.findByIdentityCode(identity_code);

        if(gymEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(userDetailsManager.userExists(dto.getUsername())){
            return ResponseEntity.badRequest().body("User already exists");
        }
        manager.createUser(CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .birth(dto.getBirth())
                .gender(dto.getGender())
                .grade("default")
                .border("red")
                .role("트레이너")
                .build()
        );
        // User(트레이너) <- Gym 관계 설정
        Optional<UserEntity> userEntity = userRepository.findByUsername(dto.getUsername());

        UserEntity user = userEntity.get();

        user.setGym(gymEntity.get());

        userRepository.save(user);
        return ResponseEntity.ok("헬스트레이너 registration successful");
    }

    // 로그인
    public String login(LoginDto dto){
        UserDetails userDetails = manager.loadUserByUsername(dto.getUsername());

        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return jwtTokenUtils.generateToken(userDetails);
    }

}
