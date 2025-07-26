package org.farsight.rag.memory;

import com.fasterxml.jackson.core.type.TypeReference;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatMemory(RedisTemplate<String, Object> objectRedisTemplate,
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
        setToRedis(conversationId, List.of(message));


    }

    /**
     * 添加多条数据到Redis中
     * 先从redis中提取数据,如果不存在则创建
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList = getFromRedis(conversationId);
        messageList.addAll(messages);

        setToRedis(conversationId,messages);


    }

    /**
     * 从Redis中获取数据,
     * 从Redis中获取倒数lastN条数据
     * @param conversationId
     * @return
     */
    @Override
    public List<Message> get(String conversationId) {
        List<Message> messageList = getFromRedis(conversationId);
        return messageList.stream()
                .toList();
    }

    /**
     * 清空数据
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(conversationId);

    }

    /**
     * 从Redis获取数据工具方法
     * @param conversationId
     * @return
     */
    private List<Message> getFromRedis(String conversationId){
        Object obj =  redisTemplate.opsForValue().get(conversationId);
        List<Message> messageList  = new ArrayList<>();
        if(obj != null){
            try {
                List<String> list = objectMapper.convertValue(obj, new TypeReference<>() {});
                for (String s : list) {
                    Message message = MessageSerializer.deserialize(s);
                    messageList.add(message);
                }
            } catch (Exception e) {
                log.error("Error deserializing messages from Redis", e);
            }
        }
        return messageList;
    }


    /**
     * 将数据存入Redis工具方法
     * @param conversationId
     * @param messages
     */
    private void setToRedis(String conversationId,List<Message> messages){
        List<String> stringList = new ArrayList<>();
        for (Message message : messages) {
            String serialize = MessageSerializer.serialize(message);
            stringList.add(serialize);
        }
        redisTemplate.opsForValue().set(conversationId,stringList);
    }


}



