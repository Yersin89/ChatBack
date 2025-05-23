package kz.spring.springboot.chat.Service.Handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.spring.springboot.chat.Dto.WebSocketResponse;
import kz.spring.springboot.chat.Entity.Chat;
import kz.spring.springboot.chat.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateChatCommandProcessor implements CommandProcessor {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String command) {
        return "createChat".equalsIgnoreCase(command);
    }

    @Override
    public boolean process(WebSocketSession session, JsonNode jsonNode) {
        try {
            if (!jsonNode.has("name") || !jsonNode.has("participants")) {
                return sendResponse(session, WebSocketResponse.error("Недостаточно параметров для создания чата"));
            }

            String chatName = jsonNode.get("name").asText();
            List<String> participants = objectMapper.convertValue(jsonNode.get("participants"), List.class);
            Chat chat = chatService.createChat(chatName, participants);

            return sendResponse(session, WebSocketResponse.success("Чат создан", "chatId", chat.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean sendResponse(WebSocketSession session, WebSocketResponse response) throws Exception {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return true;
        }
        return false;
    }
}
