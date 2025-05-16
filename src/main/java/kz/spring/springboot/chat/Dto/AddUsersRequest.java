package kz.spring.springboot.chat.Dto;

import lombok.Data;

import java.util.List;

@Data
public class AddUsersRequest {

    private List<String> participantIds;

}
