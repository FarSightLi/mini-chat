package org.farsight.rag.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/qa")
public class QaController {
    private final ZhiPuAiChatModel zhiPuAiChatModel;
    private final VectorStore vectorStore;

    @Autowired
    public QaController(
             ZhiPuAiChatModel zhiPuAiChatModel,
             VectorStore vectorStore) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
        this.vectorStore = vectorStore;
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
}