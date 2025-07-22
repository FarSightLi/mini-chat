package org.farsight.rag.llm.impl;

import org.farsight.rag.llm.LlmService;

import java.util.List;

public class OpenAiLlmService implements LlmService {
    @Override
    public String generateResponse(String prompt, List<String> context) {
        // 实现与OpenAI API的集成
        // 1. 构建包含上下文的提示词
        // 2. 调用OpenAI API
        // 3. 返回生成的响应
        return null;
    }
}