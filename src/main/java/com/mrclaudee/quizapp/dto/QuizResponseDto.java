package com.mrclaudee.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizResponseDto {
    private Integer id;
    private String response;
}
