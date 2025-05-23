package kz.spring.springboot.chat.Mapper;

import kz.spring.springboot.chat.Dto.IncomingMessage;
import kz.spring.springboot.chat.Entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "message", target = "text")  // Так как в DTO поле message, а в Entity — text
    Message toMessage(IncomingMessage incoming);
}
