package kz.spring.springboot.chat.Mapper;

import kz.spring.springboot.chat.Dto.UserDto;
import kz.spring.springboot.chat.Entity.User;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
