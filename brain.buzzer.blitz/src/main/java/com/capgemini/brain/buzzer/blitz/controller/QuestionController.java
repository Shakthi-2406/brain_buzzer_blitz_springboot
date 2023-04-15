import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.brain.buzzer.blitz.model.Question;
import com.capgemini.brain.buzzer.blitz.repository.QuestionRepository;


@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        List<Question> questions = questionRepository.findByCategory(category);
        if (questions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (questions.size() > 10) {
            questions = questions.subList(0, 10);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }
}