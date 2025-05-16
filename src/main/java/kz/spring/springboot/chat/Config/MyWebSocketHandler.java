package kz.spring.springboot.chat.Config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.spring.springboot.chat.Entity.Chat;
import kz.spring.springboot.chat.Service.ChatService;
import kz.spring.springboot.chat.Service.MessageService;
import kz.spring.springboot.chat.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;

    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdToUserId = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Новое подключение: sessionId = " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            System.out.println("Получено сообщение: " + payload);

            if (jsonNode.has("command")) {
                String command = jsonNode.get("command").asText();

                if ("register".equals(command) && jsonNode.has("userId") && jsonNode.has("name")) {
                    String userId = jsonNode.get("userId").asText();
                    String username = jsonNode.get("name").asText();

                    activeSessions.put(userId, session);
                    sessionIdToUserId.put(session.getId(), userId);
                    userService.setUserOnlineStatus(username, true);

                    System.out.printf("Пользователь %s зарегистрирован (userId=%s, sessionId=%s)%n", username, userId, session.getId());
                    session.sendMessage(new TextMessage("{\"status\":\"Пользователь зарегистрирован\"}"));
                    return;
                }

                if ("createChat".equals(command) && jsonNode.has("name") && jsonNode.has("participants")) {
                    String chatName = jsonNode.get("name").asText();
                    List<String> participants = objectMapper.readValue(jsonNode.get("participants").toString(), List.class);

                    Chat chat = chatService.createChat(chatName, participants);
                    session.sendMessage(new TextMessage("{\"status\":\"Чат создан\", \"chatId\":\"" + chat.getId() + "\"}"));
                    return;
                }
            }

            if (jsonNode.has("name") && jsonNode.has("message") && jsonNode.has("chatId")) {
                String senderName = jsonNode.get("name").asText();
                String chatId = jsonNode.get("chatId").asText();
                String content = jsonNode.get("message").asText();

                String senderId = userService.getUserIdByUsername(senderName);
                if (senderId == null) {
                    session.sendMessage(new TextMessage("{\"error\":\"Пользователь не найден\"}"));
                    return;
                }

                System.out.printf("Пользователь %s (userId=%s) отправил сообщение в чат %s: %s%n",
                        senderName, senderId, chatId, content);

                messageService.sendMessage(chatId, senderId, senderName, content);

                activeSessions.putIfAbsent(senderId, session);

                String outgoingMessage = objectMapper.writeValueAsString(Map.of(
                        "senderName", senderName,
                        "senderId", senderId,
                        "message", content,
                        "chatId", chatId,
                        "timestamp", System.currentTimeMillis()
                ));

                Chat chat = chatService.getChatById(chatId);
                if (chat == null || chat.getParticipantIds() == null) {
                    session.sendMessage(new TextMessage("{\"error\":\"Чат не найден или не содержит участников\"}"));
                    return;
                }

                for (String participantId : chat.getParticipantIds()) {
                    if (participantId.equals(senderId)) continue;

                    String participantUsername = userService.getUsernameById(participantId);
                    if (participantUsername == null) continue;

                    WebSocketSession recipientSession = activeSessions.get(participantId);
                    if (recipientSession != null && recipientSession.isOpen()) {
                        recipientSession.sendMessage(new TextMessage(outgoingMessage));
                        System.out.printf("Сообщение доставлено участнику %s (userId=%s)%n", participantUsername, participantId);
                    } else {
                        System.out.printf("Сессия участника %s (userId=%s) не найдена или закрыта.%n", participantUsername, participantId);
                    }
                }

                return;
            }

            session.sendMessage(new TextMessage("{\"error\": \"Неверный формат сообщения\"}"));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            sendSafe(session, "{\"error\": \"Ошибка парсинга JSON\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendSafe(session, "{\"error\": \"Ошибка обработки сообщения\"}");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        String userId = sessionIdToUserId.remove(sessionId);

        if (userId != null) {
            WebSocketSession removed = activeSessions.remove(userId);
            String username = userService.getUsernameById(userId);
            if (username != null) {
                userService.setUserOnlineStatus(username, false);
            }
            System.out.printf("Соединение закрыто: userId=%s, username=%s%n", userId, username);
        } else {
            System.out.println("Соединение закрыто: sessionId=" + sessionId + " (неизвестный пользователь)");
        }
    }

    private void sendSafe(WebSocketSession session, String message) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
