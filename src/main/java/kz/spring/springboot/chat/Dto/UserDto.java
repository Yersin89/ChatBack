package kz.spring.springboot.chat.Dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String name;
    private Boolean online;

    public UserDto(String id, String name, Boolean online) {
        this.id = id;
        this.name = name;
    }

}