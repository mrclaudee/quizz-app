package com.mrclaudee.quizapp.controller;

import com.mrclaudee.quizapp.model.Question;
import com.mrclaudee.quizapp.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Question>> getQuestionByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addQuestion(@RequestBody Question question) {
        return questionService.save(question);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteQuestionById(@PathVariable Integer id) {
        return questionService.deleteById(id);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateQuestion(@RequestBody Question question) {
        return questionService.save(question);
    }
}
