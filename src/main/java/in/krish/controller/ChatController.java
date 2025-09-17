package in.krish.controller;


import in.krish.entity.ChatMessage;
import in.krish.entity.ChatSession;
import in.krish.impl.ChatService;
import in.krish.impl.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final OpenAIService openAIService;

    public ChatController(ChatService chatService, OpenAIService openAIService) {
        this.chatService = chatService;
        this.openAIService = openAIService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startChat(@RequestParam Long userId) {
        ChatSession session = chatService.createSession(userId);
        return ResponseEntity.ok(Map.of("sessionId", session.getSessionId()));
    }

    @PostMapping("/reply")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String userMessage = request.get("message");

        // Save user message
        chatService.saveMessage(sessionId, "USER", userMessage);

        // Get AI reply
        String aiReply = openAIService.generateReply(userMessage);

        // Save AI reply
        chatService.saveMessage(sessionId, "AI", aiReply);

        return ResponseEntity.ok(Map.of("reply", aiReply));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestParam String sessionId) {
        return ResponseEntity.ok(chatService.getMessages(sessionId));
    }
}

