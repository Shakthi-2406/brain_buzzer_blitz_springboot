package com.capgemini.brain.buzzer.blitz.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;
    
    @Column
    private String profession;
    
    @Column
    private Long graduation_year;
    
    @Column
    private String institute;
    
    @Column
    private String stream;
    
    @Column
    private int ratings;
    
    @Column
    private int brain_coins;

	public User() {
		
	}

	public User(String username, String password, String name, String profession, Long graduation_year,
			String institute, String stream) {
		super();
		this.username = username;
		this.password = password;
		this.name = name;
		this.profession = profession;
		this.graduation_year = graduation_year;
		this.institute = institute;
		this.stream = stream;
		this.ratings = 0;
		this.brain_coins = 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public Long getGraduation_year() {
		return graduation_year;
	}

	public void setGraduation_year(Long graduation_year) {
		this.graduation_year = graduation_year;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public int getRatings() {
		return ratings;
	}

	public void setRatings(int ratings) {
		this.ratings = ratings;
	}

	public int getBrain_coins() {
		return brain_coins;
	}

	public void setBrain_coins(int brain_coins) {
		this.brain_coins = brain_coins;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", name=" + name
				+ ", profession=" + profession + ", graduation_year=" + graduation_year + ", institute=" + institute
				+ ", stream=" + stream + ", ratings=" + ratings + ", brain_coins=" + brain_coins + "]";
	}
	
}

