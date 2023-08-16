package com.example.team18project.category.gym.entities;
import com.example.team18project.category.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;


// 관장이 우리 사이트 홍보물을 보고 전화를 한다. -> 우리가 고유번호를 생성해서 gym 테이블에 넣어준다.
@Data
@Entity
@Table(name = "gym")
public class GymEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false, unique = true)
    private String location;

    private String created_at;

    @Column(nullable = false, unique = true)
    private String identityCode;

    // gym 전화번호 추가해야함
}
