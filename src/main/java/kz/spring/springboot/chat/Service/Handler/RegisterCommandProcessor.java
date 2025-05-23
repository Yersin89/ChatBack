package kz.spring.springboot.chat.Service.Handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.spring.springboot.chat.Dto.WebSocketResponse;
import kz.spring.springboot.chat.Enum.CommandType;
import kz.spring.springboot.chat.Service.UserService;
import kz.spring.springboot.chat.Service.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterCommandProcessor implements CommandProcessor {

    private final WebSocketSessionManager sessionManager;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String command) {
        return CommandType.REGISTER.getValue().equalsIgnoreCase(command);
    }

    @Override
    public boolean process(WebSocketSession session, JsonNode jsonNode) {
        if (!isValid(jsonNode)) {
            return sendResponse(session, WebSocketResponse.error("Недостаточно параметров для регистрации"));
        }

        String userId = jsonNode.get("userId").asText();
        String name = jsonNode.get("name").asText();

        try {
            sessionManager.registerSession(userId, session);
            userService.setUserOnlineStatus(name, true);

            log.info("Пользователь {} зарегистрирован (userId={}, sessionId={})", name, userId, session.getId());

            return sendResponse(session, WebSocketResponse.success("Пользователь зарегистрирован"));
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя", e);
            return sendResponse(session, WebSocketResponse.error("Ошибка сервера при регистрации"));
        }
    }

    private boolean isValid(JsonNode jsonNode) {
        return jsonNode.hasNonNull("userId") && jsonNode.hasNonNull("name");
    }

    private boolean sendResponse(WebSocketSession session, WebSocketResponse response) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                return true;
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения", e);
        }
        return false;
    }
}
