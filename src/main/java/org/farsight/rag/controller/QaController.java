package org.farsight.rag.controller;

import org.farsight.rag.memory.RedisChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import org.farsight.rag.service.ConversationService;
import org.farsight.rag.model.ConversationHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/qa")
public class QaController {
    private final ZhiPuAiChatModel zhiPuAiChatModel;
    private final VectorStore vectorStore;
    private final ConversationService conversationService;
    private final RedisChatMemory redisChatMemory;
    private final ChatClient chatClient; // 添加为字段

    @Autowired
    public QaController(
            ZhiPuAiChatModel zhiPuAiChatModel,
            VectorStore vectorStore,
            ConversationService conversationService,
            RedisChatMemory redisChatMemory) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
        this.vectorStore = vectorStore;
        this.conversationService = conversationService;
        this.redisChatMemory = redisChatMemory;
        this.chatClient = ChatClient.builder(zhiPuAiChatModel).build();
    }


    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.zhiPuAiChatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var prompt = new Prompt(new UserMessage(message));
        return this.zhiPuAiChatModel.stream(prompt);
    }

    @GetMapping("/ai/rag")
    public Map<String, Object> ragGenerate(@RequestParam(value = "message", defaultValue = "Tell me about Spring AI") String message) {
        // 1. 首先从向量存储中检索相关文档
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(3)
                        .build()
        );

        // 2. 将检索到的文档内容组合成上下文
        String context = relevantDocs.stream()
                .map(doc -> doc.toString()) // 使用toString()方法获取内容
                .collect(Collectors.joining("\n\n"));

        // 3. 构建带有上下文的提示词
        PromptTemplate promptTemplate = new PromptTemplate(
                "请根据下面提供的上下文信息回答问题。如果上下文信息不足够回答问题，请说明无法基于提供的信息回答。\n\n"
                        + "上下文信息:\n{context}\n\n"
                        + "问题: {question}\n\n"
                        + "请根据上下文回答问题:"
        );

        Prompt prompt = promptTemplate.create(Map.of(
                "context", context,
                "question", message
        ));

        // 4. 使用智谱AI模型生成回答
        ChatResponse response = this.zhiPuAiChatModel.call(prompt);
        String generation = response.getResult().getOutput().toString(); // 使用toString()方法获取内容

        // 5. 返回结果，包括生成的回答和使用的文档
        return Map.of(
                "question", message,
                "generation", generation,
                "relevant_documents", relevantDocs
        );
    }

    @GetMapping("/ai/pro")
    public Flux<String> chatPro(
            @RequestParam(value = "message", defaultValue = "How are you") String message,
            @RequestParam(value = "conversationId", required = false) String conversationId) {

        // 如果没有提供对话ID，则生成一个新的
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString();
        }

        PromptTemplate customPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                                 <query>
                        
                                 Context information is below.
                        
                        ---------------------
                        <question_answer_context>
                        ---------------------
                        
                        Given the context information and no prior knowledge, answer the query.
                        
                        Follow these rules:
                        
                        1. 如果答案不在上下文中，用你自己的知识回答
                        2. 如果答案在上下文中，要告诉用户是依据什么回答的
                        3. 展示你看到的上下文
                                \s""")
                .build();

        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(customPromptTemplate)
                // 从向量数据库中搜索
                .searchRequest(SearchRequest.builder().topK(3).build())
                .build();

        // 创建消息列表
        List<Message> messages = new ArrayList<>();

        // 添加当前用户消息
        messages.add(new UserMessage(message));

        // 返回流式响应，并在完成后保存对话历史
        String finalConversationId = conversationId;
        StringBuilder allResponse = new StringBuilder();
        return chatClient
                .prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(redisChatMemory)
                        .conversationId(finalConversationId)
                        .build())
                // 注意：advisors会按顺序执行
                .advisors(qaAdvisor)
                .stream()
                .content()
//                .chatResponse()
                .doOnNext(allResponse::append)
                .doOnComplete(()->{
//                    messages.add(new AssistantMessage(allResponse.toString()));
//                    redisChatMemory.add(finalConversationId, messages);
                });
    }
}