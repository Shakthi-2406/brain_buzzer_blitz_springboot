package com.capgemini.brain.buzzer.blitz.controller;

import com.capgemini.brain.buzzer.blitz.model.Buzzer;
import com.capgemini.brain.buzzer.blitz.model.Question;
import com.capgemini.brain.buzzer.blitz.model.User;
import com.capgemini.brain.buzzer.blitz.repository.BuzzerRepository;
import com.capgemini.brain.buzzer.blitz.repository.QuestionRepository;
import com.capgemini.brain.buzzer.blitz.repository.UserRepository;
import com.capgemini.brain.buzzer.blitz.service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://localhost:8081"})
@RestController
@RequestMapping("/buzzers")
public class BuzzerController {

    @Autowired
    private BuzzerRepository buzzerRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionService questionService;
    
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    
    public void sendToAll(String message) {
        messagingTemplate.convertAndSend("/all/messages", message);
    }
    
    //joined result buzzered began
    
    public void notifyUser(String functionality, String message, String username) {
    	String topic = "/func/" + functionality + "/" + username;
    	messagingTemplate.convertAndSend(topic, message);
    }

    
    public void sendMessageToUser(String message, String username) {
        String topic = "/specific/" + username;
        messagingTemplate.convertAndSend(topic, message);
    }


    @GetMapping("/create/{username}")
    public Buzzer createBuzzer(@PathVariable String username, @RequestParam String category, @RequestParam String difficulty, @RequestParam int count) throws Exception {
        User player1 = userRepository.findByUsername(username)
        		.orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Buzzer buzzer = new Buzzer();
        buzzer.setPlayer1(player1);
        buzzer.setDateTime(getMyTime());
        buzzer.setPlayer1Score(0);
        buzzer.setPlayer2Score(0);
        buzzer.setGameState("ACTIVE");
        buzzer.setCategory(category);
        buzzer.setDifficulty(difficulty);
        buzzer.setCurrentQuestion(0);
        
        Pageable pageable = PageRequest.of(0, count); // Retrieve the first 10 questions
        List<Question> questions = questionService.findByCategoryAndDifficultyLike(category, difficulty, pageable);
        buzzer.setQuestions(questions);
        buzzerRepository.save(buzzer);
        
        Map<String, String> map = new HashMap<>();
        
        map.put("category", category);
        map.put("difficulty", difficulty);
        map.put("buzzer_id", buzzer.getId() + "");
        map.put("dateTime", buzzer.getDateTime());
        map.put("gameState", buzzer.getGameState());
        map.put("count", count + "");
        map.put("player1", username);
        map.put("player1Ratings", player1.getRatings() + "");
        map.put("player1Institute", player1.getInstitute());
        map.put("player1Profession", player1.getProfession());

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        System.out.println(json);
        sendToAll(json);
        return buzzer;
    }

    
    @GetMapping("/join/{id}/{username}")
    public ResponseEntity<Buzzer> joinBuzzer(@PathVariable String username, @PathVariable long id) throws Exception {
        User player2 = userRepository.findByUsername(username)
        		.orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        
        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));
        
        if(buzzer.getPlayer2() != null ) return new ResponseEntity<>(buzzer, HttpStatus.BAD_REQUEST);
        
        User player1 = buzzer.getPlayer1();
        buzzer.setPlayer2(player2);
        buzzer.setGameState("READY");
        
        buzzerRepository.save(buzzer);
        
        Map<String, String> map = new HashMap<>();
        
        map.put("buzzer_id", buzzer.getId() + "");
        map.put("player1", player1.getUsername());
        map.put("player1Ratings", player1.getRatings() + "");
        map.put("player1Institute", player1.getInstitute());
        map.put("player1Profession", player1.getProfession());
        map.put("player2", username);
        map.put("player2Ratings", player2.getRatings() + "");
        map.put("player2Institute", player2.getInstitute());
        map.put("player2Profession", player2.getProfession());
        map.put("dateTime", getMyTime());
        map.put("gameState", buzzer.getGameState());   
        map.put("category", buzzer.getCategory());
        map.put("difficulty", buzzer.getDifficulty());   

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        System.out.println(json);
        
        notifyUser("joined", json, player1.getUsername());
        notifyUser("joined", json, player2.getUsername());
        
        return new ResponseEntity<>(buzzer, HttpStatus.OK);
    }
    
    @GetMapping("/begin/{id}")
    public Buzzer beginBuzzer(@PathVariable long id) throws Exception {
        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));
        
        buzzer.setGameState("IN_PROGRESS");
        buzzer.setDateTime(getMyTime());
        buzzer.setCurrentQuestion(0);
        buzzerRepository.save(buzzer);
        
        Map<String, String> map = new HashMap<>();
        
        map.put("buzzer_id", buzzer.getId() + "");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        System.out.println(json);
        
        notifyUser("began", json, buzzer.getPlayer1().getUsername());
        notifyUser("began", json, buzzer.getPlayer2().getUsername());
        
        return buzzer;
    }
    
    @PostMapping("/{id}/questionpassed/{questionIndex}")
    public Buzzer questionPassed(@PathVariable long id, @PathVariable int questionIndex) {
        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));
        int qInDB = buzzer.getCurrentQuestion();
        if(questionIndex == qInDB) buzzer.setCurrentQuestion(questionIndex+1);
        buzzerRepository.save(buzzer);
        return buzzer;
    }
    
    @PostMapping("/buzzer/{id}/{username}")
    public synchronized Buzzer buzzerBuzzer(@RequestParam int questionIndex, @RequestParam boolean correct, @RequestParam int score, @PathVariable String username, @PathVariable long id) throws Exception {
    	
        User player = userRepository.findByUsername(username)
        		.orElseThrow(() -> new IllegalArgumentException("User not found"));
        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));
        
        if(!buzzer.getGameState().equals("IN_PROGRESS")) return buzzer;
        
        if(questionIndex != buzzer.getCurrentQuestion()) {
        	System.out.println("Already buzzered!!");
        	return buzzer;
        }
        
        User opponent = buzzer.getOpponentPlayer(player);
        
        if(correct) buzzer.updatePlayerScore(player, score);
        else buzzer.updatePlayerScore(opponent, score);
        
        buzzer.setCurrentQuestion(buzzer.getCurrentQuestion()+1);
        
        buzzerRepository.save(buzzer);
        
        Map<String, String> map = new HashMap<>();
        
        map.put("buzzer_id", buzzer.getId() + "");
        map.put("player1Score", buzzer.getPlayer1Score() + "");
        map.put("player2Score", buzzer.getPlayer2Score() + "");
        map.put("score", score + "");
        map.put("buzzeredBy", player.getUsername());
        map.put("correct", ((correct) ? "correct" : "incorrect"));
        map.put("buzzeredAt", getMyExactTime());
     

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        System.out.println(json);
        
        notifyUser("buzzered", json, buzzer.getPlayer1().getUsername());
        notifyUser("buzzered", json, buzzer.getPlayer2().getUsername());
        
        return buzzer;
    }

    @PostMapping("/result/{id}")
    public synchronized Buzzer resultBuzzer(@PathVariable long id) throws Exception {
    	

        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));
        
        if(buzzer.getGameState().equals("INACTIVE")) return buzzer;
        
        buzzer.setGameState("INACTIVE");
        buzzerRepository.save(buzzer);
        
        
        String winner = null;
        User player1 = buzzer.getPlayer1();
        User player2 = buzzer.getPlayer2();
        boolean d = buzzer.getPlayer1Score() > buzzer.getPlayer2Score();
        
        Map<String, String> map = new HashMap<>();
        map.put("buzzer_id", buzzer.getId() + "");
        map.put("player1RatingsOld", player1.getRatings() + "");
        map.put("player2RatingsOld", player2.getRatings() + "");
        
        if(buzzer.getPlayer1Score() != buzzer.getPlayer2Score()) {
        	float[] updatedRatings = calculateEloRating((float)player1.getRatings(), (float)player2.getRatings(), d);
        	player1.setRatings(Math.round(updatedRatings[0]));
        	player2.setRatings(Math.round(updatedRatings[1]));
        	
        	userRepository.save(player1);
        	userRepository.save(player2);
        }else winner = "Draw";
        
        
        winner = (d) ? player1.getUsername() : player2.getUsername();
        if(buzzer.getPlayer1Score() == buzzer.getPlayer2Score()) winner = "draw";
        
        map.put("player1RatingsNew", player1.getRatings() + "");
        map.put("player2RatingsNew", player2.getRatings() + "");
        map.put("player1Score", buzzer.getPlayer1Score() + "");
        map.put("player2Score", buzzer.getPlayer2Score() + "");
        map.put("winner", winner);
        map.put("EndedAt", getMyExactTime());
     

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        System.out.println(json);
        
        notifyUser("result", json, buzzer.getPlayer1().getUsername());
        notifyUser("result", json, buzzer.getPlayer2().getUsername());
        
        return buzzer;
    }
    
    
    private float probability(float rating1, float rating2) {
        return 1.0f / (1.0f + (float) Math.pow(10, (rating2 - rating1) / 400));
    }
    
    private float[] calculateEloRating(float rating1, float rating2, boolean d) {
    	int K = 37;
        float[] ratings = new float[2];
        float p1 = probability(rating1, rating2);
        float p2 = probability(rating2, rating1);
        if (d) {
            ratings[0] = rating1 + K * (1 - p1);
            ratings[1] = rating2 + K * (0 - p2);
        }
        else {
            ratings[0] = rating1 + K * (0 - p1);
            ratings[1] = rating2 + K * (1 - p2);
        }
        return ratings;
    }
    
    @GetMapping("/questions/{id}")
    public List<Question> getQuestions(@PathVariable Long id){
        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));
        return buzzer.getQuestions();
    }
    
    @GetMapping()
    public List<Buzzer> getAllBuzzers() {
    	return buzzerRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Buzzer getBuzzerById(@PathVariable Long id) {
    	return buzzerRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Buzzer updateBuzzer(@PathVariable Long id, @RequestBody Buzzer buzzer) {
        buzzer.setId(id);
        return buzzerRepository.save(buzzer);
    }

    @DeleteMapping("/{id}")
    public void deleteBuzzer(@PathVariable Long id) {
        buzzerRepository.deleteById(id);
    }

    private String getMyTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String formatted = now.format(formatter);
        return formatted;
    }
    private String getMyExactTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        String formatted = now.format(formatter);
        return formatted;
    }
}

