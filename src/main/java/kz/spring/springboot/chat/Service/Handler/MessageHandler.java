package kz.spring.springboot.chat.Service.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.spring.springboot.chat.Dto.IncomingMessage;
import kz.spring.springboot.chat.Dto.OutgoingMessage;
import kz.spring.springboot.chat.Dto.WebSocketResponse;
import kz.spring.springboot.chat.Entity.Chat;
import kz.spring.springboot.chat.Service.ChatService;
import kz.spring.springboot.chat.Service.MessageService;
import kz.spring.springboot.chat.Service.UserService;
import kz.spring.springboot.chat.Service.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionManager sessionManager;
    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;

    public boolean handle(WebSocketSession session, IncomingMessage incomingMessage) {
        try {
            validateIncomingMessage(incomingMessage);

            String senderName = incomingMessage.getSenderName();
            String chatId = incomingMessage.getChatId();
            String content = incomingMessage.getMessage();

            String senderId = getSenderIdOrThrow(senderName);

            messageService.sendMessage(chatId, senderId, senderName, content);
            sessionManager.registerSession(senderId, session);

            Chat chat = getChatOrThrow(chatId);
            validateChatParticipants(chat);

            String outgoingMessage = buildMessageJson(senderName, senderId, content, chatId);
            sendToChatParticipants(chat, senderId, outgoingMessage);

            return true;
        } catch (IllegalArgumentException e) {
            sendError(session, e.getMessage());
            return true;
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения", e);
            sendError(session, "Внутренняя ошибка сервера");
            return false;
        }
    }

    private void validateIncomingMessage(IncomingMessage message) {
        if (message.getSenderName() == null || message.getSenderName().isEmpty()
                || message.getMessage() == null || message.getMessage().isEmpty()
                || message.getChatId() == null || message.getChatId().isEmpty()) {
            throw new IllegalArgumentException("Недостаточно параметров для отправки сообщения");
        }
    }

    private String getSenderIdOrThrow(String senderName) {
        return Optional.ofNullable(userService.getUserIdByUsername(senderName))
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    private Chat getChatOrThrow(String chatId) {
        return Optional.ofNullable(chatService.getChatById(chatId))
                .orElseThrow(() -> new IllegalArgumentException("Чат не найден"));
    }

    private void validateChatParticipants(Chat chat) {
        if (chat.getParticipantIds() == null || chat.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Чат не содержит участников");
        }
    }

    private String buildMessageJson(String senderName, String senderId, String content, String chatId) {
        try {
            OutgoingMessage outgoingMessage = new OutgoingMessage(senderName, senderId, content, chatId);
            return objectMapper.writeValueAsString(outgoingMessage);
        } catch (Exception e) {
            log.error("Ошибка сериализации сообщения", e);
            return errorJson("Ошибка при формировании сообщения");
        }
    }

    private void sendToChatParticipants(Chat chat, String senderId, String outgoingMessage) {
        for (String participantId : chat.getParticipantIds()) {
            if (participantId.equals(senderId)) continue;

            WebSocketSession recipientSession = sessionManager.getSessionByUserId(participantId);
            if (recipientSession != null && recipientSession.isOpen()) {
                sendSafe(recipientSession, outgoingMessage);
                log.debug("Сообщение доставлено участнику (userId={})", participantId);
            } else {
                log.warn("Сессия пользователя (userId={}) не найдена или закрыта", participantId);
            }
        }
    }

    private void sendSafe(WebSocketSession session, String json) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(json));
            }
        } catch (IOException e) {
            log.error("Ошибка отправки WebSocket-сообщения", e);
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            if (session != null && session.isOpen()) {
                WebSocketResponse errorResponse = WebSocketResponse.error(errorMessage);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResponse)));
            }
        } catch (IOException e) {
            log.error("Ошибка отправки ошибки WebSocket-сообщения", e);
        }
    }

    private String errorJson(String message) {
        try {
            return objectMapper.writeValueAsString(java.util.Map.of("error", message));
        } catch (Exception e) {
            return "{\"error\":\"" + message + "\"}";
        }
    }
}

