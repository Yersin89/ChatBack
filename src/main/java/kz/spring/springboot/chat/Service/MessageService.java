package kz.spring.springboot.chat.Service;

import kz.spring.springboot.chat.Entity.Message;


import java.util.List;


public interface MessageService {
    Message sendMessage(String chatId, String senderId, String senderName, String text);
    List<Message> getMessagesByChatId(String chatId);
}