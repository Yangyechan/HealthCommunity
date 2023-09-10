package com.example.team18project.category.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class C_CommentDto {
    @NotBlank
    private String comment;
}
