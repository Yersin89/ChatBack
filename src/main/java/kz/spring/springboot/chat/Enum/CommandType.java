package kz.spring.springboot.chat.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommandType {
    REGISTER("register"),
    CREATE_CHAT("createChat");

    private final String value;


}
