package com.capgemini.brain.buzzer.blitz.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.brain.buzzer.blitz.exception.ResourceNotFoundException;
import com.capgemini.brain.buzzer.blitz.model.Buzzer;
import com.capgemini.brain.buzzer.blitz.model.Question;
import com.capgemini.brain.buzzer.blitz.repository.QuestionRepository;
import com.capgemini.brain.buzzer.blitz.service.QuestionService;

@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080", "http://localhost:8081" })
@RestController
@RequestMapping("/questions")
public class QuestionController {
	
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionService questionService;

    
    @GetMapping("/streams")
    public ResponseEntity<Map<String, List<String>>> findAllStreamsAndCategories() {
		Map<String, List<String>> result = questionService.findAllStreamsAndCategories();
        return ResponseEntity.ok(result);
    } 
    
    // Get all questions
    @GetMapping("")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Create a new question
    @PostMapping("")
    public Question createQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }
    
    @PostMapping("/questions")
    public ResponseEntity<String> addQuestions(@RequestBody List<Question> questions) {
        // Code to add questions to the database
        questionRepository.saveAll(questions);
        return ResponseEntity.ok("Questions added successfully");
    }
  
    
    @GetMapping("/individual/questions")
    public List<Question> getQuestions(@RequestParam String stream,@RequestParam String categoryLike, @RequestParam String difficulty, @RequestParam int count) throws Exception {
    	Pageable pageable = PageRequest.of(0, count); // Retrieve the first 10 questions
        List<Question> questions = questionService.findByStreamAndDifficultyLikeAndCategoryLike(stream, difficulty, categoryLike, pageable);
    	return questions;
    }
    
    
    // Get a single question by id
    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable(value = "id") Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
    }

        
    // Update a question
    @PutMapping("/{id}")
    public Question updateQuestion(@PathVariable(value = "id") Long questionId,
                                   @RequestBody Question questionDetails) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        question.setText(questionDetails.getText());
        question.setOptions(questionDetails.getOptions());
        question.setAnswer(questionDetails.getAnswer());
        question.setCategory(questionDetails.getCategory());
        question.setStream(questionDetails.getStream());
        question.setDifficulty(questionDetails.getDifficulty());

        return questionRepository.save(question);
    }

    // Delete a question
    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable(value = "id") Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        questionRepository.delete(question);
    }
}
