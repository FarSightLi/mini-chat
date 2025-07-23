package org.farsight.rag.model;

import lombok.Data;
import org.springframework.ai.chat.messages.Message;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationHistory {
    private String conversationId;
    private List<Message> messages;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    
    public ConversationHistory() {
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
    }
    
    public ConversationHistory(String conversationId, List<Message> messages) {
        this.conversationId = conversationId;
        this.messages = messages;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
    }

    public List<Message> getMessages() {
        return messages;
    }
}