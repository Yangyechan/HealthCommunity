package com.example.team18project.category.rental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Comment_Dto {
    @NotBlank
    private String comment;
}
