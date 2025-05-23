package kz.spring.springboot.chat.Service.Handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.socket.WebSocketSession;

public interface CommandProcessor {
    boolean supports(String command);
    boolean process(WebSocketSession session, JsonNode jsonNode);
}
