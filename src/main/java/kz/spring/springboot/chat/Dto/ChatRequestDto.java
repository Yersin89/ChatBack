package kz.spring.springboot.chat.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequestDto{
    private String name;
    private List<String> participantIds;
}