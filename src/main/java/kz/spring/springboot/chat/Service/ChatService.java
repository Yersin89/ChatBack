package kz.spring.springboot.chat.Service;

import kz.spring.springboot.chat.Dto.ChatDTO;
import kz.spring.springboot.chat.Entity.Chat;

import java.util.List;

public interface ChatService {

    public List<Chat> getAllChats();

    Chat createChat(String name, List<String> participantIds);


    List<ChatDTO> getChatsForUser(String userId);
    public void deleteUser(String id);


    public Chat addUsersToChat(String chatId, List<String> participantIds);

    public void deleteChat(String chatId);

    public Chat removeUserFromChat(String chatId, String userId);

    Chat getChatById(String chatId);
}
