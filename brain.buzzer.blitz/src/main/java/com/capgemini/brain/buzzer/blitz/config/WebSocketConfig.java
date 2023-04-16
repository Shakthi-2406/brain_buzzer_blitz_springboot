package com.capgemini.brain.buzzer.blitz.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/all","/specific"); //to where users can register and receive 
        											  //message updates
        config.setApplicationDestinationPrefixes("/app"); //to where users can send
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	registry.addEndpoint("/websocket")
        	.setAllowedOrigins("http://localhost:8080", "http://example.com");
    	
        registry.addEndpoint("/websocket")
            .setAllowedOrigins("http://localhost:8080", "http://example.com")
            .withSockJS();
    }
}