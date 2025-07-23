package org.farsight.rag.config;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZhiPuAiConfig {
    @Value("${spring.ai.zhipuai.api-key}") // 从配置文件读取密钥
    private String apiKey;

    @Bean
    public ZhiPuAiApi zhiPuAiApi() {
        return new ZhiPuAiApi(apiKey);
    }

    @Bean
    public ZhiPuAiChatModel zhiPuAiChatModel(ZhiPuAiApi zhiPuAiApi) {
        // 手动创建实例并配置参数
        return new ZhiPuAiChatModel(zhiPuAiApi);
    }

    @Bean
    public EmbeddingModel embeddingModel(ZhiPuAiApi zhiPuAiApi) {
        return new ZhiPuAiEmbeddingModel(zhiPuAiApi, MetadataMode.EMBED,
                ZhiPuAiEmbeddingOptions.builder()
                        .model("embedding-3")
                        .dimensions(1536)
                        .build());
    }
}