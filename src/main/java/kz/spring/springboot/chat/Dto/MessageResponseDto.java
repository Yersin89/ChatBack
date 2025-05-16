package kz.spring.springboot.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponseDto {
    private String senderId;
    private String senderName;
    private String text;
    private String timestamp;
}
