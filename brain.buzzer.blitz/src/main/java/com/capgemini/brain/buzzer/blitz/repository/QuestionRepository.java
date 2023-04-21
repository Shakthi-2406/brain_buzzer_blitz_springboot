package com.capgemini.brain.buzzer.blitz.repository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.capgemini.brain.buzzer.blitz.model.Question;


@Repository
public interface QuestionRepository  extends JpaRepository<Question, Long> {
	List<Question> findByCategory(String category);
	List<Question> findByCategoryAndDifficulty(String category, String difficulty, Pageable pageable);
}