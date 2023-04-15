package com.capgemini.brain.buzzer.blitz.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capgemini.brain.buzzer.blitz.exception.ResourceNotFoundException;
import com.capgemini.brain.buzzer.blitz.model.Buzzer;
import com.capgemini.brain.buzzer.blitz.model.User;
import com.capgemini.brain.buzzer.blitz.repository.BuzzerRepository;

@Service
public class BuzzerService {

	@Autowired
	private BuzzerRepository buzzerRepository;
	
	public int getPlayerScore(Long buzzerId, User currentPlayer) {
		Buzzer buzzer = buzzerRepository.findById(buzzerId).orElseThrow(() -> new ResourceNotFoundException("Buzzer", "id", buzzerId));
		return buzzer.getPlayerScore(currentPlayer);
	}
	
	public User getOpponentPlayer(Long buzzerId, User currentPlayer) {
		Buzzer buzzer = buzzerRepository.findById(buzzerId).orElseThrow(() -> new ResourceNotFoundException("Buzzer", "id", buzzerId));
		return buzzer.getOpponentPlayer(currentPlayer);
	}
	
//	@Transactional
//	public synchronized void claimQuestion(String username, Long gameId) throws GameException {
//	    Game game = gameRepository.findById(gameId)
//	            .orElseThrow(() -> new GameException("Game not found"));
//	    if (game.getState() != GameState.STARTED) {
//	        throw new GameException("Game has not started yet");
//	    }
//	    if (game.getCurrentQuestionIndex() < 0 || game.getCurrentQuestionIndex() >= game.getNumQuestions()) {
//	        throw new GameException("No more questions left");
//	    }
//	    if (!game.getCurrentQuestion().getClaimedBy().equals(username)) {
//	        // Use row-level locking to ensure that only one user can claim the current question at any given time
//	        entityManager.lock(game.getCurrentQuestion(), LockModeType.PESSIMISTIC_WRITE);
//	        if (!game.getCurrentQuestion().getClaimedBy().equals(username)) {
//	            throw new GameException("Question already claimed by another user");
//	        }
//	    }
//	    game.moveToNextQuestion();
//	    gameRepository.save(game);
//	}
}
