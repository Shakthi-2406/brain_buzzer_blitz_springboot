package com.capgemini.brain.buzzer.blitz.service;
import org.springframework.beans.factory.annotation.Autowired;
import com.capgemini.brain.buzzer.blitz.exception.ResourceNotFoundException;
import com.capgemini.brain.buzzer.blitz.model.Buzzer;
import com.capgemini.brain.buzzer.blitz.model.User;
import com.capgemini.brain.buzzer.blitz.repository.BuzzerRepository;

public class BuzzerService {

	@Autowired
	private BuzzerRepository buzzerRepository;
	
	public int getPlayerScore(Long buzzerId, User currentPlayer) {
		Buzzer buzzer = buzzerRepository.findById(buzzerId).orElseThrow(() -> new ResourceNotFoundException("Buzzer", buzzerId));
		return buzzer.getPlayerScore(currentPlayer);
	}
	
	public User getOpponentPlayer(Long buzzerId, User currentPlayer) {
		Buzzer buzzer = buzzerRepository.findById(buzzerId).orElseThrow(() -> new ResourceNotFoundException("Buzzer", buzzerId));
		return buzzer.getOpponentPlayer(currentPlayer);
	}

}
