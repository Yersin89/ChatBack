package kz.spring.springboot.chat.Dto;

import lombok.Data;

@Data
public class MessageRequestDto {
    private String chatId;
    private String senderId;
    private String senderName;
    private String text;
}
