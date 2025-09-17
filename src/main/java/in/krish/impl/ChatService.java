package in.krish.impl;

import in.krish.entity.ChatMessage;
import in.krish.entity.ChatSession;
import in.krish.repo.ChatMessageRepository;
import in.krish.repo.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository messageRepo;

    public ChatService(ChatSessionRepository sessionRepo, ChatMessageRepository messageRepo) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
    }

    /**
     * Creates a new chat session for the given user.
     * Generates a unique session ID and records timestamps.
     */
    public ChatSession createSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setStartedAt(LocalDateTime.now());
        session.setLastActiveAt(LocalDateTime.now());
        return sessionRepo.save(session);
    }

    /**
     * Saves a chat message to the database and updates the session's last active time.
     */
    public void saveMessage(String sessionId, String sender, String messageContent) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSender(sender);
        message.setMessage(messageContent);
        message.setTimestamp(LocalDateTime.now());
        messageRepo.save(message);

        // Update lastActiveAt for the session
        sessionRepo.findBySessionId(sessionId).ifPresent(session -> {
            session.setLastActiveAt(LocalDateTime.now());
            sessionRepo.save(session);
        });
    }

    /**
     * Retrieves all messages for a given session, ordered by timestamp.
     */
    public List<ChatMessage> getMessages(String sessionId) {
        return messageRepo.findBySessionIdOrderByTimestampAsc(sessionId);
    }
}

