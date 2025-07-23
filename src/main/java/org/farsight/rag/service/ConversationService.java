package org.farsight.rag.service;

import org.farsight.rag.model.ConversationHistory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ConversationService {
    
    private static final String CONVERSATION_PREFIX = "conversation:";
    private static final long CONVERSATION_TTL_HOURS = 24; // 对话历史保留24小时
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 保存对话历史
     * @param conversationId 对话ID
     * @param messages 消息列表
     */
    public void saveConversation(String conversationId, List<Message> messages) {
        String key = CONVERSATION_PREFIX + conversationId;
        ConversationHistory history = new ConversationHistory(conversationId, messages);
        redisTemplate.opsForValue().set(key, history, Duration.ofHours(CONVERSATION_TTL_HOURS));
    }
    
    /**
     * 获取对话历史
     * @param conversationId 对话ID
     * @return 对话历史
     */
    public ConversationHistory getConversation(String conversationId) {
        String key = CONVERSATION_PREFIX + conversationId;
        ConversationHistory history = (ConversationHistory) redisTemplate.opsForValue().get(key);
        if (history != null) {
            // 更新最后访问时间
            history.setLastAccessedAt(LocalDateTime.now());
            redisTemplate.opsForValue().set(key, history, Duration.ofHours(CONVERSATION_TTL_HOURS));
        }
        return history;
    }
    
    /**
     * 删除对话历史
     * @param conversationId 对话ID
     */
    public void deleteConversation(String conversationId) {
        String key = CONVERSATION_PREFIX + conversationId;
        redisTemplate.delete(key);
    }
    
    /**
     * 清理过期对话（可选的后台任务）
     */
    public void cleanupExpiredConversations() {
        // Redis会自动过期，这里可以添加额外的清理逻辑
    }
}