package org.farsight.rag.vector.impl;
import org.farsight.rag.vector.VectorStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChromaDbVectorStore implements VectorStore {
//    private final EmbeddingStore<TextSegment> embeddingStore;

    public ChromaDbVectorStore() {
        // 创建 ChromaDB 客户端（默认连接 localhost:8000）
//        ChromaDbClient client = ChromaDbClient.builder().build();
//        this.embeddingStore = new ChromaDbEmbeddingStore<>(client, TextSegment.class);
    }

    @Override
    public void storeEmbeddings(String documentId, float[] embedding) {
        // 将 float[] 转换为 List<Float>
//        List<Float> embeddingList = ArrayUtils.toObject(embedding);
//        embeddingStore.add(documentId, embeddingList);
    }

    @Override
    public List<Map<String, Object>> searchSimilar(float[] queryEmbedding, int topK) {
        // 转换查询向量为 List<Float>
//        List<Float> queryVector = ArrayUtils.toObject(queryEmbedding);

        // 执行相似搜索
//        List<TextSegment> results = embeddingStore.findRelevant(queryVector, topK);

        // 返回结果（转换为 Map 形式）
//        return results.stream()
//                .map(segment -> Map.of("content", segment.text(), "score", segment.score()))
//                .collect(Collectors.toList());
        return null;
    }
}