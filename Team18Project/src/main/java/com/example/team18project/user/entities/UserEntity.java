package com.example.team18project.user.entities;

import com.example.team18project.diet_inform.entities.Diet_Inform_ArticleEntity;
import com.example.team18project.diet_inform.entities.Diet_Inform_CommentEntity;
import com.example.team18project.diet_inform.entities.Diet_Inform_DislikesEntity;
import com.example.team18project.diet_inform.entities.Diet_Inform_LikesEntity;
import com.example.team18project.free_board.entities.Free_ArticleEntity;
import com.example.team18project.free_board.entities.Free_Article_CommentEntity;
import com.example.team18project.free_board.entities.Free_Article_DislikesEntity;
import com.example.team18project.free_board.entities.Free_Article_LikesEntity;
import com.example.team18project.gym.entities.GymEntity;
import com.example.team18project.gym.entities.Gym_rateEntity;
import com.example.team18project.gym.entities.Trainer_boardEntity;
import com.example.team18project.health_inform.entities.Health_Inform_ArticleEntity;
import com.example.team18project.health_inform.entities.Health_Inform_CommentEntity;
import com.example.team18project.health_inform.entities.Health_Inform_LikesEntity;
import com.example.team18project.health_inform.entities.Health_inform_DislikesEntity;
import com.example.team18project.rental.entities.Rental_ArticleEntity;
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
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String address;

    private String profile_img;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    private String birth;

    private String gender;

    private String grade;

    private String border;

    private Integer role;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private GymEntity gym;

    private String verification_img;

    @OneToMany(mappedBy = "user")
    private List<Gym_rateEntity> gym_rates = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Trainer_boardEntity> trainer_boards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rental_ArticleEntity> rentalArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Free_ArticleEntity> freeArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Free_Article_DislikesEntity> freeArticleDislikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Free_Article_CommentEntity> freeArticleComments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Free_Article_LikesEntity> freeArticleLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Diet_Inform_ArticleEntity> dietInformArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Diet_Inform_CommentEntity> dietInformComments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Diet_Inform_DislikesEntity> dietInformDislikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Diet_Inform_LikesEntity> dietInformLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Health_Inform_ArticleEntity> healthInformArticles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Health_Inform_CommentEntity> healthInformComments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Health_inform_DislikesEntity> healthInformDislikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Health_Inform_LikesEntity> healthInformLikes = new ArrayList<>();
}
