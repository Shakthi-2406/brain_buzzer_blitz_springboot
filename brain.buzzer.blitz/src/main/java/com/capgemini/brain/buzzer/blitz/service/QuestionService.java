package com.capgemini.brain.buzzer.blitz.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.capgemini.brain.buzzer.blitz.model.Question;
import com.capgemini.brain.buzzer.blitz.repository.QuestionRepository;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Map<String, List<String>> findAllStreamsAndCategories() {
        Map<String, List<String>> result = new HashMap<>();
        List<String> streams = questionRepository.findAllStreams();
        for (String stream : streams) {
            List<String> categories = questionRepository.findAllCategoriesByStream(stream);
            result.put(stream, categories);
        }
        return result;
    }
    
    
    public List<Question> findByStreamAndDifficultyLikeAndCategoryLike(String stream, String difficulty, String categoryLike, Pageable pageable) {
        List<Question> questions = questionRepository.findByStreamAndDifficultyLikeAndCategoryLike(stream, difficulty, categoryLike);
        Collections.shuffle(questions);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), questions.size());
        return questions.subList(start, end);
    }

}