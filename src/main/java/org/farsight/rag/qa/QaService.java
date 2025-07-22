package org.farsight.rag.qa;

import java.util.List;

public class QaService {
    private final DocumentProcessor documentProcessor;
    private final VectorStore vectorStore;
    private final LlmService llmService;

    public QaService(DocumentProcessor documentProcessor, VectorStore vectorStore, LlmService llmService) {
        this.documentProcessor = documentProcessor;
        this.vectorStore = vectorStore;
        this.llmService = llmService;
    }

    public String answerQuestion(String question) {
        // 1. Vectorize question
        // 2. Search similar documents
        // 3. Generate response with LLM
        return "Answer placeholder";
    }
}