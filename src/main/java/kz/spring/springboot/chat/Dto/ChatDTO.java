package kz.spring.springboot.chat.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatDTO {
    private String id;
    private String name;
    private List<String> participantIds;
    private List<String> participantNames;
}
