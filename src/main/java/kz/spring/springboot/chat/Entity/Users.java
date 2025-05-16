package kz.spring.springboot.chat.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Document(collection = "products")
public class Users {
    @Id
    private String id;
    private String name;
    private Boolean online = false;
}
