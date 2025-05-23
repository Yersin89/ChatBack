package kz.spring.springboot.chat.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private Boolean online = false;
}
