package com.capgemini.brain.buzzer.blitz.repository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.capgemini.brain.buzzer.blitz.model.Question;


@Repository
public interface QuestionRepository  extends JpaRepository<Question, Long> {
	
	List<Question> findByCategory(String category);
	
	List<Question> findByCategoryAndDifficultyLike(String category, String difficultyLike);
	
	
    @Query("SELECT DISTINCT q.stream FROM Question q")
    List<String> findAllStreams();

    @Query("SELECT DISTINCT q.category FROM Question q WHERE q.stream = :stream")
    List<String> findAllCategoriesByStream(@Param("stream") String stream);
}