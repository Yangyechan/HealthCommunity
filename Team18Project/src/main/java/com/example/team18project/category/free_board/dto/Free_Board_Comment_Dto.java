package com.example.team18project.category.free_board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Free_Board_Comment_Dto {
        @NotBlank
        private String comment;
    }


