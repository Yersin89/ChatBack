package kz.spring.springboot.chat.Controller;

import kz.spring.springboot.chat.Dto.MessageRequestDto;
import kz.spring.springboot.chat.Dto.MessageResponseDto;
import kz.spring.springboot.chat.Entity.Message;
import kz.spring.springboot.chat.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponseDto> sendMessage(@RequestBody MessageRequestDto request) {
        Message message = messageService.sendMessage(
                request.getChatId(),
                request.getSenderId(),
                request.getSenderName(),
                request.getText()
        );

        return ResponseEntity.ok(new MessageResponseDto(
                message.getSenderId(),
                message.getSenderName(),
                message.getText(),
                message.getTimestamp()
        ));
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageResponseDto>> getMessages(@PathVariable String chatId) {
        List<MessageResponseDto> messages = messageService.getMessagesByChatId(chatId)
                .stream()
                .map(msg -> new MessageResponseDto(
                        msg.getSenderId(),
                        msg.getSenderName(),
                        msg.getText(),
                        msg.getTimestamp()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }
}
