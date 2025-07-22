package org.farsight.rag.document;

import org.farsight.rag.entity.dto.Document;

import java.util.List;

public interface DocumentProcessor {
    List<Document> process(String filePath);
}

