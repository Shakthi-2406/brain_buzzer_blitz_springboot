package com.capgemini.brain.buzzer.blitz.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/all","/func","/specific"); //to where users can register and receive 
        											  //message updates
        config.setApplicationDestinationPrefixes("/app"); //to where users can send
    }

    
    public void registerCorsConfiguration(CorsRegistry registry) {
        registry.addMapping("/websocket/**")
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:5500/", "http://localhost:8080")
                .allowedMethods("GET", "POST")
                .allowCredentials(true);
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	registry.addEndpoint("/websocket")
        	.setAllowedOrigins("http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:5500/");
    	
        registry.addEndpoint("/websocket")
            .setAllowedOrigins("http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:5500/")
            .withSockJS();
    }
}