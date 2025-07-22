package org.farsight.rag.llm;

import java.util.List;

public interface LlmService {
    String generateResponse(String prompt, List<String> context);
}