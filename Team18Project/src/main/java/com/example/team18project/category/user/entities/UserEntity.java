package com.example.team18project.category.user.entities;

import com.example.team18project.category.challenge.entities.Challenge_ArticleEntity;
import com.example.team18project.category.challenge.entities.Challenge_CommentEntity;
import com.example.team18project.category.challenge.entities.Challenge_LikesEntity;
import com.example.team18project.category.free_board.entities.Free_ArticleEntity;
import com.example.team18project.category.free_board.entities.Free_Article_CommentEntity;
import com.example.team18project.category.gym.entities.GymEntity;
import com.example.team18project.category.gym.entities.Gym_rateEntity;
import com.example.team18project.category.gym.entities.Trainer_boardEntity;
import com.example.team18project.category.health_inform.entities.Health_Inform_ArticleEntity;
import com.example.team18project.category.health_inform.entities.Health_Inform_CommentEntity;
import com.example.team18project.category.health_inform.entities.Health_Inform_LikesEntity;
import com.example.team18project.category.rental.entities.Rental_ArticleEntity;
import com.example.team18project.category.rental.entities.Rental_CommentEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  // 아이디

    @Column(nullable = false)
    private String password;  // 비밀번호

    @Column(nullable = false, unique = true)
    private String nickname;  // 별명

    private String address;   // 주소

    private String profileImg; // 개인 프로필

    private String email;  //  ex) example@naver.com

    private String phone;  // 010-1234-5678

    private String birth;  // 8자리 EX) 19981212

    private String gender; // W / M

    private String grade;  // 이건 알고리즘으로 switch case 문으로 구현할 예정 등급제

    private String border; // 테두리 1. 일반회원 검은색 2. 전문가 빨간색 3. 관리자 초록색

    private String role;
    // 1. 일반회원  2. 트레이너 3. 관장   4. 관리자
    // 1. general 2. trainer 3. enema 4. admin
    private String identityCode; // 고유 번호

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private GymEntity gym;

    @OneToMany(mappedBy = "user")
    private List<Gym_rateEntity> gym_rates = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Trainer_boardEntity> trainer_boards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rental_ArticleEntity> rentalArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Free_ArticleEntity> freeArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Free_Article_CommentEntity> freeArticleComments = new ArrayList<>();


    @OneToMany(mappedBy = "user")
    private List<Health_Inform_ArticleEntity> healthInformArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Health_Inform_CommentEntity> healthInformComments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Health_Inform_LikesEntity> healthInformLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Challenge_ArticleEntity> challengeArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Challenge_CommentEntity> challengeComments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Challenge_LikesEntity> challengeLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rental_CommentEntity> rentalComments = new ArrayList<>();


}
