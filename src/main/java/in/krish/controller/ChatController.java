package in.krish.controller;

import in.krish.impl.OpenAIService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import org.springframework.http.codec.ServerSentEvent;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAIService aiChatService;

    public ChatController(OpenAIService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * Synchronous AI reply (full response)
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String aiReply = aiChatService.generateReply(userMessage);
        return ResponseEntity.ok(Map.of("reply", aiReply));
    }

    /**
     * Streaming AI reply (SSE) - realtime typing effect
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        // Stream tokens from AI
        return aiChatService.streamReply(userMessage)
                .map(token -> ServerSentEvent.builder(token)
                        .event("token")
                        .build())
                .doOnError(err -> System.err.println("SSE stream error: " + err.getMessage()))
                .concatWith(Flux.just(ServerSentEvent.builder("[DONE]").event("done").build()));
    }
}