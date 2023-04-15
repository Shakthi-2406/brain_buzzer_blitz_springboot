package com.capgemini.brain.buzzer.blitz.model;

import java.util.List;
import javax.persistence.*;


@Entity
@Table(name = "question")
public class Question {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;
    
    @ElementCollection
    @Column(nullable = false)
    private List<String> options;
    
    @Column(nullable = false)
    private String answer;
    
    @Column
    private String category;
    
    @Column
    private String stream;

    @Column
    private String difficulty;
    
    public Question() {
    	
    }

	public Question(String text, List<String> options, String answer, String category, String stream, String difficulty) {
		super();
		this.text = text;
		this.options = options;
		this.answer = answer;
		this.category = category;
		this.stream = stream;
		this.difficulty = difficulty;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCategory() {
		return category;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
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

	@Override
	public String toString() {
		return "Question [id=" + id + ", text=" + text + ", options=" + options + ", answer=" + answer + ", category="
				+ category + ", difficulty=" + difficulty + "]";
	}
    
}
