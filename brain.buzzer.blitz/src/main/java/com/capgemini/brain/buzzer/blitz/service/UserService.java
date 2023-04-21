package com.capgemini.brain.buzzer.blitz.service;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.capgemini.brain.buzzer.blitz.exception.ResourceNotFoundException;
import com.capgemini.brain.buzzer.blitz.model.User;
import com.capgemini.brain.buzzer.blitz.repository.UserRepository;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User createUser(User user) {
//        if (userRepository.findByUsername(user.getUsername()) != null) {
//            throw new DuplicateKeyException("Username already exists");
//        }
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
 
    public User getUserByUsername(String username) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
    }
    
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        user.setName(updatedUser.getName());
        user.setProfession(updatedUser.getProfession());
        user.setGraduation_year(updatedUser.getGraduation_year());
        user.setInstitute(updatedUser.getInstitute());
        user.setStream(updatedUser.getStream());
        user.setRatings(updatedUser.getRatings());
        user.setBrain_coins(updatedUser.getBrain_coins());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}

