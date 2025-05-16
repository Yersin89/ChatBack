package kz.spring.springboot.chat.Service.Impl;

import kz.spring.springboot.chat.Dto.ChatDTO;
import kz.spring.springboot.chat.Entity.Chat;
import kz.spring.springboot.chat.Entity.Message;
import kz.spring.springboot.chat.Entity.Users;
import kz.spring.springboot.chat.Exception.ChatNotFoundException;
import kz.spring.springboot.chat.Exception.UserNotFoundException;
import kz.spring.springboot.chat.Repository.ChatRepository;
import kz.spring.springboot.chat.Repository.MessageRepository;
import kz.spring.springboot.chat.Repository.UsersRepository;
import kz.spring.springboot.chat.Service.ChatService;
import kz.spring.springboot.chat.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final UsersRepository userRepository;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Override
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
    @Override
    public Chat createChat(String name, List<String> participantIds) {
        Collections.sort(participantIds);

        Optional<Chat> existingChat = chatRepository.findByParticipantIdsContainingAll(participantIds);

        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        Chat chat = new Chat();
        chat.setName(name);
        chat.setParticipantIds(participantIds);
        return chatRepository.save(chat);
    }

    @Override
    public Chat getChatByName(String name) {
        return chatRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Override
    public List<ChatDTO> getChatsForUser(String userId) {
        List<Chat> chats = chatRepository.findByParticipantIdsContains(userId);
        List<ChatDTO> chatDTOs = new ArrayList<>();

        for (Chat chat : chats) {
            List<String> participantNames = chat.getParticipantIds().stream()
                    .filter(id -> !id.equals(userId)) // Исключаем текущего пользователя
                    .map(id -> userService.getUserById(id) // Получаем пользователя по ID
                            .map(Users::getName)
                            .orElse("Неизвестный пользователь"))
                    .collect(Collectors.toList());

            String chatName = String.join(", ", participantNames);

            ChatDTO chatDTO = new ChatDTO();
            chatDTO.setId(chat.getId());
            chatDTO.setName(chatName);
            chatDTO.setParticipantIds(chat.getParticipantIds());
            chatDTO.setParticipantNames(participantNames);

            chatDTOs.add(chatDTO);
        }

        return chatDTOs;
    }




    @Override
    public void sendMessageToChat(String chatId, String senderId, String messageText) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Message message = Message.builder()
                .chatId(chatId)
                .senderId(senderId)
                .text(messageText)
                .timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .build();

        messageRepository.save(message);
    }
    @Override
    public void deleteUser(String id) {
        chatRepository.deleteById(id);
    }
    @Override
    public Chat addUsersToChat(String chatId, List<String> participantIds) {
        // Получаем чат по ID
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ChatNotFoundException("Чат не найден");
        }

        Chat chat = chatOptional.get();

        // Получаем всех пользователей, которых нужно добавить
        List<Users> usersToAdd = userRepository.findAllById(participantIds);
        if (usersToAdd.size() != participantIds.size()) {
            throw new UserNotFoundException("Один или несколько пользователей не найдены");
        }

        // Добавляем новых участников в чат
        for (Users user : usersToAdd) {
            if (!chat.getParticipantIds().contains(user.getId())) {
                chat.getParticipantIds().add(user.getId());
            }
        }

        // Сохраняем обновленный чат
        return chatRepository.save(chat);
    }
    @Override
    public void deleteChat(String chatId) {
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ChatNotFoundException("Чат не найден");
        }

        chatRepository.deleteById(chatId);
    }

    @Override
    public Chat removeUserFromChat(String chatId, String userId) {
        // Получаем чат по ID
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ChatNotFoundException("Чат не найден");
        }

        Chat chat = chatOptional.get();

        // Проверяем, есть ли пользователь в списке участников
        if (!chat.getParticipantIds().contains(userId)) {
            throw new UserNotFoundException("Пользователь не найден в чате");
        }

        // Удаляем пользователя из списка участников
        chat.getParticipantIds().remove(userId);

        // Сохраняем обновленный чат
        return chatRepository.save(chat);
    }

    @Override
    public Chat getChatById(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат с id " + chatId + " не найден"));
    }



}
