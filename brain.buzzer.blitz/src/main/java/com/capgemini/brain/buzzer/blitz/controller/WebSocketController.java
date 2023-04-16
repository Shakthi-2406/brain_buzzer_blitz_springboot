package com.capgemini.brain.buzzer.blitz.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
    @MessageMapping("/specific/{username}") //to which users can send
    public void sendMessageToUserFromUser(@Payload String message, @DestinationVariable String username) {
        String topic = "/specific/" + username;
        messagingTemplate.convertAndSend(topic, message);
    }

    @MessageMapping("/all") //to which users can send
    @SendTo("/all/messages") //where users will register to receive
    public String sendToAllFromUser(final String message) throws Exception {
        return message;
    }
    
    @SendTo("/all/messages") //where users will register to receive
    public String sendToAll(final String message) throws Exception {
        return message;
    }
    
    // sending from administrator
    public void sendMessageToUser(String message, String username) {
        String topic = "/specific/" + username;
        messagingTemplate.convertAndSend(topic, message);
    }
}
