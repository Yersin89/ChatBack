package kz.spring.springboot.chat.Service.Impl;

import kz.spring.springboot.chat.Dto.ChatDTO;
import kz.spring.springboot.chat.Entity.Chat;
import kz.spring.springboot.chat.Entity.User;
import kz.spring.springboot.chat.Exception.ChatNotFoundException;
import kz.spring.springboot.chat.Exception.UserNotFoundException;
import kz.spring.springboot.chat.Repository.ChatRepository;
import kz.spring.springboot.chat.Repository.UsersRepository;
import kz.spring.springboot.chat.Service.ChatService;
import kz.spring.springboot.chat.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final UsersRepository userRepository;
    private final UserService userService;
    private final ChatRepository chatRepository;

    @Override
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
    @Override
    public Chat createChat(String name, List<String> participantIds) {
        Collections.sort(participantIds);

        Optional<Chat> existingChat = chatRepository.findByParticipantIdsContainingAll(participantIds);
        return existingChat.orElseGet(() ->
                chatRepository.save(Chat.builder()
                        .name(name)
                        .participantIds(participantIds)
                        .build())
        );
    }

    @Override
    public List<ChatDTO> getChatsForUser(String userId) {
        return chatRepository.findByParticipantIdsContains(userId).stream()
                .map(chat -> {
                    List<String> participantNames = chat.getParticipantIds().stream()
                            .filter(id -> !id.equals(userId))
                            .map(id -> userService.getUserById(id)
                                    .map(User::getName)
                                    .orElse("Неизвестный пользователь"))
                            .collect(Collectors.toList());

                    return ChatDTO.builder()
                            .id(chat.getId())
                            .name(String.join(", ", participantNames))
                            .participantIds(chat.getParticipantIds())
                            .participantNames(participantNames)
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Override
    public void deleteUser(String id) {
        chatRepository.deleteById(id);
    }

    @Override
    public Chat addUsersToChat(String chatId, List<String> participantIds) {
        Chat chat = getChatOrThrow(chatId);
        List<User> usersToAdd = getValidUsers(participantIds);

        usersToAdd.stream()
                .map(User::getId)
                .filter(id -> !chat.getParticipantIds().contains(id))
                .forEach(chat.getParticipantIds()::add);

        return chatRepository.save(chat);
    }

    @Override
    public void deleteChat(String chatId) {
        getChatOrThrow(chatId);
        chatRepository.deleteById(chatId);
    }

    @Override
    public Chat removeUserFromChat(String chatId, String userId) {
        Chat chat = getChatOrThrow(chatId);

        if (!chat.getParticipantIds().remove(userId)) {
            throw new UserNotFoundException("Пользователь не найден в чате");
        }

        return chatRepository.save(chat);
    }

    @Override
    public Chat getChatById(String chatId) {
        return getChatOrThrow(chatId);
    }
    private Chat getChatOrThrow(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Чат с id " + chatId + " не найден"));
    }

    private List<User> getValidUsers(List<String> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new UserNotFoundException("Один или несколько пользователей не найдены");
        }
        return users;
    }
}
