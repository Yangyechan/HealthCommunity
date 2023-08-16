package com.example.team18project.security.details;

import com.example.team18project.category.user.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class CustomUserDetails implements UserDetails {

        @Getter
        private Long id;
        private String username;
        private String password;
        private String nickname;
        private String address;
        private String email;
        private String phone;
        private String profile_img;
        private String birth;
        private String gender;
        private String grade;
        private String border;
        private String role;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public String getPassword() {
            return this.password;
        }

        @Override
        public String getUsername() {
            return this.username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        @Override
        public boolean isEnabled() {
            return true;
        }

        public static CustomUserDetails fromEntity(UserEntity entity) {
            return CustomUserDetails.builder()
                    .id(entity.getId())
                    .username(entity.getUsername())
                    .password(entity.getPassword())
                    .nickname(entity.getNickname())
                    .address(entity.getAddress())
                    .email(entity.getEmail())
                    .phone(entity.getPhone())
                    .profile_img(entity.getProfile_img())
                    .birth(entity.getBirth())
                    .gender(entity.getGender())
                    .grade(entity.getGrade())
                    .border(entity.getBorder())
                    .role(entity.getRole())
                    .build();
        }

        public UserEntity newEntity() {
            UserEntity entity = new UserEntity();
            entity.setUsername(username);
            entity.setPassword(password);
            entity.setNickname(nickname);
            entity.setAddress(address);
            entity.setEmail(email);
            entity.setPhone(phone);
            entity.setProfile_img(profile_img);
            entity.setBirth(birth);
            entity.setGender(gender);
            entity.setBorder(border);
            entity.setRole(role);
            return entity;
        }


}
