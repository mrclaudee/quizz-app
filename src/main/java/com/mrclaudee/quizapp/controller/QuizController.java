package com.mrclaudee.quizapp.controller;

import com.mrclaudee.quizapp.dto.QuestionDto;
import com.mrclaudee.quizapp.dto.QuizResponseDto;
import com.mrclaudee.quizapp.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(@RequestParam String category, @RequestParam int numQ, @RequestParam String title) {
        return quizService.create(category, numQ, title);
    }

    @GetMapping("/get/{quizId}")
    public ResponseEntity<List<QuestionDto>> getQuizQuestions(@PathVariable int quizId) {
        return quizService.getById(quizId);
    }

    @PostMapping("/submit/{id}")
    public ResponseEntity<Integer> submit(@PathVariable int id, @RequestBody List<QuizResponseDto> quizResponseDtos) {
        return quizService.calculateResult(id, quizResponseDtos);
    }
}
