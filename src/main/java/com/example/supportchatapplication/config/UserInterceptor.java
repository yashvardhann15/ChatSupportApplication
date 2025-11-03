package com.example.supportchatapplication.config;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map; // <-- IMPORT THIS

@Component
public class UserInterceptor implements ChannelInterceptor {

    // 1. CREATE A CONSTANT KEY
    public static final String USER_ID_SESSION_KEY = "user_id_key";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = accessor.getLogin(); // This is the Agent or Customer ID

            if (userId != null && !userId.isEmpty()) {

                // 2. GET THE SESSION ATTRIBUTES MAP
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

                if (sessionAttributes != null) {
                    // 3. STORE THE ID MANUALLY
                    sessionAttributes.put(USER_ID_SESSION_KEY, userId);
                }

                // 4. We still set the user, as it's good practice
                accessor.setUser(() -> userId);
            }
        }
        return message;
    }
}