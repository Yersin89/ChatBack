package kz.spring.springboot.chat.Dto;

import lombok.*;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WebSocketResponse {

    private String status;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    public static WebSocketResponse success(String message) {
        return WebSocketResponse.builder()
                .status("success")
                .message(message)
                .build();
    }

    public static WebSocketResponse success(String message, String key, Object value) {
        return WebSocketResponse.builder()
                .status("success")
                .message(message)
                .data(Map.of(key, value))
                .build();
    }

    public static WebSocketResponse error(String message) {
        return WebSocketResponse.builder()
                .status("error")
                .message(message)
                .build();
    }
}
