package org.farsight.rag.document;

import java.util.List;

public interface DocumentProcessor {
    List<Document> process(String filePath);
}

class Document {
    private String content;
    private String metadata;
    
    // Getters and setters
    
    public Document(String content, String metadata) {
        this.content = content;
        this.metadata = metadata;
    }
}