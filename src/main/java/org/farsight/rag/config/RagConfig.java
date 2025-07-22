package org.farsight.rag.config;

import org.farsight.rag.document.DocumentProcessor;
import org.farsight.rag.document.impl.PdfDocumentProcessor;
import org.farsight.rag.vector.VectorStore;
import org.farsight.rag.vector.impl.ChromaDbVectorStore;
import org.farsight.rag.llm.LlmService;
import org.farsight.rag.llm.impl.OpenAiLlmService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    
    // 配置文档处理器
    @Bean
    public DocumentProcessor documentProcessor() {
        return new PdfDocumentProcessor();
    }
    
    // 配置向量数据库
    @Bean
    public VectorStore vectorStore() {
        return new ChromaDbVectorStore();
    }
    
    // 配置LLM服务
    @Bean
    public LlmService llmService() {
        return new OpenAiLlmService();
    }
}