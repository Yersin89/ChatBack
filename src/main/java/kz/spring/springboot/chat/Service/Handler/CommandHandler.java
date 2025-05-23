package kz.spring.springboot.chat.Service.Handler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandHandler {

    private final List<CommandProcessor> processors;

    public boolean handle(WebSocketSession session, JsonNode jsonNode) {
        if (!jsonNode.has("command")) return false;

        String command = jsonNode.get("command").asText();

        for (CommandProcessor processor : processors) {
            if (processor.supports(command)) {
                return processor.process(session, jsonNode);
            }
        }

        return false;
    }
}
