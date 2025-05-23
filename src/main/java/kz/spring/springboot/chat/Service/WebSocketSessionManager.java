package kz.spring.springboot.chat.Service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdToUserId = new ConcurrentHashMap<>();

    public void registerSession(String userId, WebSocketSession session) {
        activeSessions.put(userId, session);
        sessionIdToUserId.put(session.getId(), userId);
    }

    public WebSocketSession getSessionByUserId(String userId) {
        return activeSessions.get(userId);
    }

    public String getUserIdBySessionId(String sessionId) {
        return sessionIdToUserId.get(sessionId);
    }

    public void removeSession(WebSocketSession session) {
        String sessionId = session.getId();
        String userId = sessionIdToUserId.remove(sessionId);
        if (userId != null) {
            activeSessions.remove(userId);
        }
    }

    public boolean isUserConnected(String userId) {
        WebSocketSession session = activeSessions.get(userId);
        return session != null && session.isOpen();
    }
}
