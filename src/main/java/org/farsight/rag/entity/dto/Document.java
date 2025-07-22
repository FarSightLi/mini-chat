package org.farsight.rag.entity.dto;

import lombok.Data;

@Data
public class Document {
    private String content;
    private String metadata;

    public Document(String content, String metadata) {
        this.content = content;
        this.metadata = metadata;
    }
}
