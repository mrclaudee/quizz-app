package com.mrclaudee.quizapp.service;

import com.mrclaudee.quizapp.model.Question;
import com.mrclaudee.quizapp.repository.QuestionRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepo repo;

    public QuestionService(QuestionRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(repo.findAllByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> save(Question question) {
        try {
            repo.save(question);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("cannot save question", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> deleteById(Integer id) {
        try {
            repo.deleteById(id);
            return new ResponseEntity<>("success", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("cannot delete question", HttpStatus.BAD_REQUEST);
    }
}
