package com.mrclaudee.quizapp.repository;

import com.mrclaudee.quizapp.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepo extends JpaRepository<Quiz, Integer> {
}
