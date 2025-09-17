package in.krish.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    /**
     * Generate AI reply using OpenAI Chat Completions API.
     *
     * @param userMessage The message sent by the user
     * @return AI generated reply
     */
    public String generateReply(String userMessage) {
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo"); // Or "gpt-4"

        // The messages array with role and content
        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", userMessage)
        );
        requestBody.put("messages", messages);

        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 150);

        try {
            // Call OpenAI API
            return webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
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
                        return "Sorry, I couldn't generate a response.";
                    })
                    .block(); // Blocking call to wait for the response
        } catch (WebClientResponseException e) {
            // Log API errors
            e.printStackTrace();
            return "Error: Unable to generate AI response.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unexpected error occurred while generating AI response.";
        }
    }
}
