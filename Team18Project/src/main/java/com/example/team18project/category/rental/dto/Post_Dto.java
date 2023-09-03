package com.example.team18project.category.rental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Post_Dto {
    @NotBlank
    private String tag;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
