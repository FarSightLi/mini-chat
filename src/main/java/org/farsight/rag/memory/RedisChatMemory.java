package org.farsight.rag.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.farsight.rag.config.MessageSerializer;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Redis持久化
 */
@Service
@Slf4j
public class RedisChatMemory implements ChatMemory {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Integer MAX_HISTORY_SIZE = 10;

    public RedisChatMemory(RedisTemplate<String, String> objectRedisTemplate,
                           ObjectMapper objectMapper){
        this.redisTemplate = objectRedisTemplate;
        this.objectMapper = objectMapper;
    }




    /**
     * 添加一个数据到Redis中
     * @param conversationId
     * @param message
     */
    @Override
    public void add(String conversationId, Message message) {
        String serializedMessage = MessageSerializer.serialize(message);
        // 使用 Redis List 的 RPUSH 命令在列表右侧添加元素
        redisTemplate.opsForList().rightPush(conversationId, serializedMessage);
        // 限制列表大小，只保留最新的 MAX_HISTORY_SIZE 条记录
        redisTemplate.opsForList().trim(conversationId, -MAX_HISTORY_SIZE, -1);
        log.info("Adding message to Redis: {} with id: {}", message, conversationId);


    }

    /**
     * 添加多条数据到Redis中
     * 先从redis中提取数据,如果不存在则创建
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<String> serializedMessages = new ArrayList<>();
        for (Message message : messages) {
            serializedMessages.add(MessageSerializer.serialize(message));
        }
        // 使用 Redis List 的 RPUSH 命令在列表右侧添加多个元素
        redisTemplate.opsForList().rightPushAll(conversationId, serializedMessages);
        // 限制列表大小，只保留最新的 MAX_HISTORY_SIZE 条记录
        redisTemplate.opsForList().trim(conversationId, -MAX_HISTORY_SIZE, -1);


    }

    /**
     * 从Redis中获取数据,
     * 从Redis中获取倒数lastN条数据
     * @param conversationId
     * @return
     */
    @Override
    public List<Message> get(String conversationId) {
        List<Message> messageList = new ArrayList<>();
        try {
            // 使用 List 操作获取所有元素
            List<String> serializedMessages = redisTemplate.opsForList().range(conversationId, 0, -1);
            if (serializedMessages != null) {
                for (String serializedMessage : serializedMessages) {
                    Message message = MessageSerializer.deserialize(serializedMessage);
                    messageList.add(message);
                }
            }
        } catch (Exception e) {
            log.error("Error deserializing messages from Redis", e);
        }
        return messageList;
    }

    /**
     * 清空数据
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(conversationId);

    }
}



