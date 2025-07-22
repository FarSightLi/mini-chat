package org.farsight.rag.document.impl;

import org.farsight.rag.document.Document;
import org.farsight.rag.document.DocumentProcessor;

import java.util.List;

public class PdfDocumentProcessor implements DocumentProcessor {
    @Override
    public List<Document> process(String filePath) {
        // 实现PDF文档处理逻辑
        // 1. 读取PDF文件
        // 2. 分割文本块
        // 3. 返回文档列表
        return null;
    }
}