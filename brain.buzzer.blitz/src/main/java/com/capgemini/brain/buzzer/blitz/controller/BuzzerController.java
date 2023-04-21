package com.capgemini.brain.buzzer.blitz.controller;

import com.capgemini.brain.buzzer.blitz.model.Buzzer;
import com.capgemini.brain.buzzer.blitz.model.Question;
import com.capgemini.brain.buzzer.blitz.model.User;
import com.capgemini.brain.buzzer.blitz.repository.BuzzerRepository;
import com.capgemini.brain.buzzer.blitz.repository.QuestionRepository;
import com.capgemini.brain.buzzer.blitz.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private QuestionRepository questionRepository;
    
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
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String formatted = now.format(formatter);
        
        Buzzer buzzer = new Buzzer();
        buzzer.setPlayer1(player1);
        buzzer.setDateTime(formatted);
        buzzer.setPlayer1Score(0);
        buzzer.setPlayer2Score(0);
        buzzer.setGameState("ACTIVE");
        buzzer.setCategory(category);
        buzzer.setDifficulty(difficulty);
        buzzer.setCurrentQuestion(0);
        
        Pageable pageable = PageRequest.of(0, count); // Retrieve the first 10 questions
        List<Question> questions = questionRepository.findByCategoryAndDifficulty(category, difficulty, pageable);
        buzzer.setQuestions(questions);
        buzzerRepository.save(buzzer);
        
        Map<String, String> map = new HashMap<>();
        
        map.put("category", category);
        map.put("difficulty", difficulty);
        map.put("buzzer_id", buzzer.getId() + "");
        map.put("dateTime", buzzer.getDateTime());
        map.put("gameState", buzzer.getGameState());
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
    public Buzzer joinBuzzer(@PathVariable String username, @PathVariable long id) throws Exception {
        User player2 = userRepository.findByUsername(username)
        		.orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        
        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));
        User player1 = buzzer.getPlayer1();
        buzzer.setPlayer2(player2);
        buzzer.setGameState("JOINED");
        
        buzzerRepository.save(buzzer);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String formatted = now.format(formatter);
        
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
        map.put("dateTime", formatted);
        map.put("gameState", buzzer.getGameState());        

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        System.out.println(json);
        
        notifyUser("joined", json, player1.getUsername());
        notifyUser("joined", json, player2.getUsername());
        
        return buzzer;
    }
    
    @GetMapping("/begin/{id}")
    public Buzzer beginBuzzer(@PathVariable long id) throws Exception {
        Buzzer buzzer = buzzerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buzzer not found"));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String formatted = now.format(formatter);
        
        buzzer.setGameState("STARTED");
        buzzer.setDateTime(formatted);
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
}

