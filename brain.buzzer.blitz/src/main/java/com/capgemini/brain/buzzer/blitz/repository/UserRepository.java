package com.capgemini.brain.buzzer.blitz.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.capgemini.brain.buzzer.blitz.model.User;


@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

}