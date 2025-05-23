package kz.spring.springboot.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingMessage {
    private String senderName;
    private String senderId;
    private String message;
    private String chatId;
    private Instant timestamp;

    public OutgoingMessage(String senderName, String senderId, String message, String chatId) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.chatId = chatId;
        this.timestamp = Instant.now();
    }
}
