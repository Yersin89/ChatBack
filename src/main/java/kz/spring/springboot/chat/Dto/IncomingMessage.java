package kz.spring.springboot.chat.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingMessage {
    private String name;
    private String chatId;
    private String message;
}
