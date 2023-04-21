package com.capgemini.brain.buzzer.blitz.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "buzzer")
public class Buzzer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", referencedColumnName = "id", nullable = false)
    private User player1;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "player2_id", referencedColumnName = "id", nullable = true)
    private User player2;
    
    @Column
    private String dateTime;
    
    @Column
    private int currentQuestion;

	@Column
    private String category;
    
    @Column
    private String difficulty;

	@Column
    private int player1Score;
    
    @Column
    private int player2Score;
    
    @Column
    private String gameState;
    
    @ManyToMany
    @JoinTable(name = "buzzer_question",
            joinColumns = @JoinColumn(name = "buzzer_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Question> questions;
    
    
    public Buzzer() {
        this.questions = new ArrayList<>();
    }
    
    public Buzzer(User player1, User player2, String dateTime, String category, int player1Score, int player2Score,
			String gameState, List<Question> questions) {
		super();
		this.player1 = player1;
		this.player2 = player2;
		this.dateTime = dateTime;
		this.category = category;
        this.player1Score = 0;
        this.player2Score = 0;
        this.gameState = "IN_PROGRESS";
		this.questions = questions;
	}
    
    public int getPlayerScore(User currentPlayer) {
        if (currentPlayer.equals(player1)) {
            return player1Score;
        } else if (currentPlayer.equals(player2)) {
            return player2Score;
        } else {
            throw new IllegalArgumentException("User not found in this buzzer game.");
        }
    }
    
    public void updatePlayerScore(User currentPlayer, int change) {
        if (currentPlayer.equals(player1)) {
            player1Score += change;
        } else if (currentPlayer.equals(player2)) {
            player2Score += change;
        } else {
            throw new IllegalArgumentException("User not found in this buzzer game.");
        }
    }
    
	// method to get the opponent user
    public User getOpponentPlayer(User currentPlayer) {
        if (currentPlayer.equals(player1)) {
            return player2;
        } else if (currentPlayer.equals(player2)) {
            return player1;
        } else {
            throw new IllegalArgumentException("User not found in this buzzer game.");
        }
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getPlayer1() {
		return player1;
	}

	public void setPlayer1(User player1) {
		this.player1 = player1;
	}

	public User getPlayer2() {
		return player2;
	}

	public void setPlayer2(User player2) {
		this.player2 = player2;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
    public int getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(int currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

    
    public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	public int getPlayer1Score() {
		return player1Score;
	}

	public void setPlayer1Score(int player1Score) {
		this.player1Score = player1Score;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	public void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}

	public String getGameState() {
		return gameState;
	}

	public void setGameState(String gameState) {
		this.gameState = gameState;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	@Override
	public String toString() {
		return "Buzzer [id=" + id + ", player1=" + player1 + ", player2=" + player2 + ", dateTime=" + dateTime
				+ ", category=" + category + ", player1Score=" + player1Score + ", player2Score=" + player2Score
				+ ", gameState=" + gameState + ", questions=" + questions + "]";
	}
    
}
