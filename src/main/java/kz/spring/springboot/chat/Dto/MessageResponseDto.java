package kz.spring.springboot.chat.Dto;

import kz.spring.springboot.chat.Entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MessageResponseDto {
    private String senderId;
    private String senderName;
    private String text;
    private String timestamp;

    public static MessageResponseDto fromMessage(Message message) {
        return new MessageResponseDto(
                message.getSenderId(),
                message.getSenderName(),
                message.getText(),
                message.getTimestamp()
        );
    }
}
