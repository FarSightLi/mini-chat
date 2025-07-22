package org.farsight.rag.vector.impl;

import org.farsight.rag.vector.VectorStore;

import java.util.List;
import java.util.Map;

public class ChromaDbVectorStore implements VectorStore {
    @Override
    public void storeEmbeddings(String documentId, float[] embedding) {
        // 实现向量存储逻辑
    }

    @Override
    public List<Map<String, Object>> searchSimilar(float[] queryEmbedding, int topK) {
        // 实现相似搜索逻辑
        return null;
    }
}