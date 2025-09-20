package in.krish.impl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OpenAIService {

    private final WebClient webClient;
    private final String apiKey;

    public OpenAIService(WebClient.Builder webClientBuilder,
                         @Value("${openai.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
    }

    /**
     * Generate full AI reply synchronously
     */
    public String generateReply(String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(Map.of("role", "user", "content", userMessage)),
                "temperature", 0.7,
                "max_tokens", 150
        );

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)))
                    .block();

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) return message.get("content").toString().trim();
            }
            return "Sorry, could not generate a response.";
        } catch (WebClientResponseException e) {
            return "OpenAI error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    /**
     * Stream AI reply token-by-token for SSE (reactive)
     */
    public Flux<String> streamReply(String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(Map.of("role", "user", "content", userMessage)),
                "temperature", 0.7,
                "max_tokens", 150,
                "stream", true
        );

        AtomicReference<StringBuilder> aggregator = new AtomicReference<>(new StringBuilder());

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(Map.class) // Each token chunk as a map
                .map(chunk -> {
                    // OpenAI streaming sends small JSON chunks with 'content'
                    Map<String, Object> delta = (Map<String, Object>) ((Map<String, Object>) ((List) chunk.get("choices")).get(0)).get("delta");
                    String token = delta != null ? (String) delta.getOrDefault("content", "") : "";
                    aggregator.get().append(token);
                    return token;
                })
                .doOnError(err -> System.err.println("Stream error: " + err.getMessage()));
    }
}