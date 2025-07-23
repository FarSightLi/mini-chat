package org.farsight.rag.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class VectorConfig {
    private final LoggingInterceptor loggingInterceptor;

    // Spring 会自动注入 LoggingInterceptor Bean
    public VectorConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Bean
    public RestClient.Builder restClientBuilder() { // 建议使用更具体的名称，例如 restClientBuilder
        return RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory());
//                .requestInterceptor(loggingInterceptor); // **这里添加拦截器**
    }

    // 假设你需要手动创建这些 Bean
    // 如果你依赖 Spring AI 的 auto-configuration，这些可以省略
    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        // 注意：这里硬编码了 URL。更好的方式是从配置中获取。
        String chromaUrl = "http://localhost:8010";
        return new ChromaApi(chromaUrl, restClientBuilder, objectMapper);
    }

    @Bean
    public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName("my_documents") // 确保这个名称与ChromaDB中实际的集合名称一致
                .initializeSchema(true) // 暂时设置为 false，以避免初始化时的复杂性
                .build();
    }
}