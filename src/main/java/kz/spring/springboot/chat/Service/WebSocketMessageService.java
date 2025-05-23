package kz.spring.springboot.chat.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.spring.springboot.chat.Dto.IncomingMessage;
import kz.spring.springboot.chat.Service.Handler.CommandHandler;
import kz.spring.springboot.chat.Service.Handler.MessageHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public class WebSocketMessageService {

    private final ObjectMapper objectMapper;
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;

    public WebSocketMessageService(ObjectMapper objectMapper,
                                   CommandHandler commandHandler,
                                   MessageHandler messageHandler) {
        this.objectMapper = objectMapper;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
    }

    public void handleMessage(WebSocketSession session, String payload) throws IOException {
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (IOException e) {
            sendError(session, "Неверный формат сообщения: не удалось разобрать JSON");
            return;
        }

        boolean handled = commandHandler.handle(session, jsonNode);

        if (!handled) {
            try {
                IncomingMessage incomingMessage = objectMapper.treeToValue(jsonNode, IncomingMessage.class);
                handled = messageHandler.handle(session, incomingMessage);
            } catch (Exception e) {
                System.err.println("Ошибка при десериализации IncomingMessage: " + e.getMessage());
                sendError(session, "Неверный формат сообщения");
                return;
            }
        }

        if (!handled) {
            sendError(session, "Неверный формат сообщения");
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) throws IOException {
        if (session != null && session.isOpen()) {
            String errorJson = String.format("{\"error\":\"%s\"}", errorMessage);
            session.sendMessage(new TextMessage(errorJson));
        }
    }
}
