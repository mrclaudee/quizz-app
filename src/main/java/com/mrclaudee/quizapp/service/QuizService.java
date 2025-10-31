package com.mrclaudee.quizapp.service;

import com.mrclaudee.quizapp.dto.QuestionDto;
import com.mrclaudee.quizapp.model.Question;
import com.mrclaudee.quizapp.model.Quiz;
import com.mrclaudee.quizapp.dto.QuizResponseDto;
import com.mrclaudee.quizapp.repository.QuestionRepo;
import com.mrclaudee.quizapp.repository.QuizRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepo quizRepo;
    private final QuestionRepo questionRepo;

    public QuizService(QuizRepo quizRepo, QuestionRepo questionRepo) {
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
    }

    public ResponseEntity<String> create(String category, int numQ, String title) {
        try {
            List<Question> questions = questionRepo.findRandomQuestionsByCategory(category, numQ);
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setQuestions(questions);
            quizRepo.save(quiz);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("cannot create quiz", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<QuestionDto>> getById(int quizId) {
        try {
            Optional<Quiz> quiz = quizRepo.findById(quizId);
            if (quiz.isPresent()) {
                List<Question> questionFromDB = quiz.get().getQuestions();
                List<QuestionDto> questionsForUser = new ArrayList<>();
                for (Question question: questionFromDB) {
                    questionsForUser.add(new QuestionDto(
                            question.getId(),
                            question.getQuestionTitle(),
                            question.getOption1(),
                            question.getOption2(),
                            question.getOption3(),
                            question.getOption4()
                    ));
                }
                return new ResponseEntity<>(questionsForUser, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Integer> calculateResult(int id, List<QuizResponseDto> quizResponseDtos) {
        Optional<Quiz> quiz = quizRepo.findById(id);
        if (quiz.isPresent()) {
            List<Question> questions = quiz.get().getQuestions();
            int right = 0;
            for (int i = 0; i < quizResponseDtos.size(); i++) {
                if (quizResponseDtos.get(i).getResponse().equals(questions.get(i).getRightAnswer()))
                    right++;
            }
            return new ResponseEntity<>(right, HttpStatus.OK);
        }
        return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
    }
}
