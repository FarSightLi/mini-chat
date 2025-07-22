package org.farsight.rag.vector;

import java.util.List;
import java.util.Map;

public interface VectorStore {
    void storeEmbeddings(String documentId, float[] embedding);
    List<Map<String, Object>> searchSimilar(float[] queryEmbedding, int topK);
}