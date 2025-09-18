package in.krish.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final WebClient webClient;
    private final String apiKey;

    public OpenAIService(WebClient.Builder webClientBuilder,
                         @Value("${openai.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
    }

    public String generateReply(String userMessage) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are a helpful assistant."),
                            Map.of("role", "user", "content", userMessage)
                    )
            );

            return webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                            if (message != null) {
                                return message.get("content").toString().trim();
                            }
                        }
                        return "⚠️ Sorry, I couldn't generate a response.";
                    })
                    // Handle 429 rate limit errors
                    .onErrorResume(WebClientResponseException.TooManyRequests.class, e -> {
                        String retryAfter = e.getHeaders().getFirst("Retry-After");
                        return Mono.just("⚠️ Rate limited by OpenAI. Please retry after "
                                + (retryAfter != null ? retryAfter + " seconds" : "a while") + ".");
                    })
                    // Handle any other errors
                    .onErrorResume(Exception.class, e ->
                            Mono.just("⚠️ Unexpected error occurred while generating AI response."))
                    .block();

        } catch (Exception e) {
            return "⚠️ Error: Unable to generate AI response.";
        }
    }
}
