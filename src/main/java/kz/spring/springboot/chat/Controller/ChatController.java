package kz.spring.springboot.chat.Controller;

import kz.spring.springboot.chat.Dto.AddUsersRequest;
import kz.spring.springboot.chat.Dto.ChatDTO;
import kz.spring.springboot.chat.Dto.ChatRequestDto;
import kz.spring.springboot.chat.Entity.Chat;
import kz.spring.springboot.chat.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/all")
    public ResponseEntity<List<Chat>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/create")
    public ResponseEntity<Chat> createChat(@RequestBody ChatRequestDto chatRequest) {
        List<String> participantIds = chatRequest.getParticipantIds();
        String name = chatRequest.getName();

        Chat chat = chatService.createChat(name, participantIds);
        return ResponseEntity.ok(chat);
    }
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        chatService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public List<ChatDTO> getChatsForUser(@PathVariable String userId) {
        return chatService.getChatsForUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable String id) {
        try {
            chatService.deleteChat(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{chatId}/addUsers")
    public ResponseEntity<Chat> addUsersToChat(@PathVariable String chatId, @RequestBody AddUsersRequest request) {
        try {
            Chat updatedChat = chatService.addUsersToChat(chatId, request.getParticipantIds());
            return ResponseEntity.ok(updatedChat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{chatId}/removeUser")
    public ResponseEntity<Chat> removeUserFromChat(
            @PathVariable String chatId,
            @RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        Chat updatedChat = chatService.removeUserFromChat(chatId, userId);
        return ResponseEntity.ok(updatedChat);
    }

}
