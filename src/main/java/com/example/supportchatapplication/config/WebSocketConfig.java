package com.example.supportchatapplication.config;

import com.example.supportchatapplication.config.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor // <-- 1. ADD THIS to create a constructor for final fields
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 2. DEFINE AND INJECT the interceptor using 'final'
    private final UserInterceptor userInterceptor;

    /**
     * Registers the STOMP endpoint the clients (Agent and Customer UIs) will use
     * to connect to the WebSocket server.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the initial handshake endpoint. Clients connect to: ws://localhost:8080/ws-connect
        registry.addEndpoint("/ws-connect")
                .setAllowedOriginPatterns("*");
    }

    /**
     * Configures the message broker that handles routing messages.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 1. Destination Prefixes for the Broker (Topics)
        // Server sends to clients. Clients subscribe to topics starting with /topic, e.g., /topic/conversation/123
        registry.enableSimpleBroker("/topic");

        // 2. Application Destination Prefixes (Controller endpoints)
        // Clients send messages TO the server. Clients send messages to endpoints starting with /app, e.g., /app/chat/send/123
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the UserInterceptor on the inbound channel to process CONNECT messages.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 3. The injected interceptor is correctly registered here.
        registration.interceptors(userInterceptor);
    }
}