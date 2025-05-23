package kz.spring.springboot.chat.Controller;

import kz.spring.springboot.chat.Dto.AddUsersRequest;
import kz.spring.springboot.chat.Dto.ChatDTO;
import kz.spring.springboot.chat.Dto.ChatRequestDto;
import kz.spring.springboot.chat.Dto.RemoveUserRequest;
import kz.spring.springboot.chat.Entity.Chat;
import kz.spring.springboot.chat.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/all")
    public ResponseEntity<List<Chat>> getAllChats() {
        return ResponseEntity.ok(chatService.getAllChats());
    }

    @PostMapping("/create")
    public ResponseEntity<Chat> createChat(@RequestBody ChatRequestDto chatRequest) {
        Chat chat = chatService.createChat(chatRequest.getName(), chatRequest.getParticipantIds());
        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteChatByUserId(@PathVariable String id) {
        chatService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatDTO>> getChatsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getChatsForUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable String id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{chatId}/addUsers")
    public ResponseEntity<Chat> addUsersToChat(
            @PathVariable String chatId,
            @RequestBody AddUsersRequest request) {
        Chat updatedChat = chatService.addUsersToChat(chatId, request.getParticipantIds());
        return ResponseEntity.ok(updatedChat);
    }

    @PutMapping("/{chatId}/removeUser")
    public ResponseEntity<Chat> removeUserFromChat(
            @PathVariable String chatId,
            @RequestBody RemoveUserRequest request) {
        Chat updatedChat = chatService.removeUserFromChat(chatId, request.getUserId());
        return ResponseEntity.ok(updatedChat);
    }
}
