package kz.spring.springboot.chat.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    private String name;
    private List<String> participantIds;

}
