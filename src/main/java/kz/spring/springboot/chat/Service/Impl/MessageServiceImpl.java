package kz.spring.springboot.chat.Service.Impl;

import kz.spring.springboot.chat.Entity.Message;
import kz.spring.springboot.chat.Repository.MessageRepository;
import kz.spring.springboot.chat.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message sendMessage(String chatId, String senderId, String senderName, String text) {
        Message message = Message.builder()
                .chatId(chatId)
                .senderId(senderId)
                .senderName(senderName)
                .text(text)
                .timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .build();

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesByChatId(String chatId) {
        return messageRepository.findByChatId(chatId);
    }
}