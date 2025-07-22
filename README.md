# 基于 RAG 的智能知识问答系统

## 项目概述

本项目是一个基于检索增强生成（RAG）架构的智能问答系统，旨在为特定领域知识提供精准回答。通过将文档内容向量化并存储在向量数据库中，系统能够高效检索相关知识片段，并结合大型语言模型（LLM）生成准确回答，有效解决了LLM的“幻觉”问题。

## 技术栈

- **Java版本**: Java 21
- **框架**: Spring Boot 3.4, Spring AI
- **LLM**: OpenAI API
- **向量数据库**: ChromaDB
- **构建工具**: Maven

## 项目结构

```
src/main/java/org/farsight/rag
├── document    # 文档处理模块
│   ├── DocumentProcessor.java
│   └── impl
│       └── PdfDocumentProcessor.java
├── vector      # 向量数据库模块
│   ├── VectorStore.java
│   └── impl
│       └── ChromaDbVectorStore.java
├── llm         # LLM集成模块
│   ├── LlmService.java
│   └── impl
│       └── OpenAiLlmService.java
├── qa          # 问答服务模块
│   └── QaService.java
├── controller  # REST API控制器
│   └── QaController.java
├── config      # 配置类
│   └── RagConfig.java
└── RagApplication.java  # 应用启动类
```

## 核心功能

1. **文档处理**：支持PDF文档的解析和处理，将内容分割为可管理的文本块。
2. **向量化与存储**：使用OpenAI的嵌入模型将文档内容转换为高维向量，并存储于ChromaDB中，支持高效的语义检索。
3. **问答流程**：用户问题经向量化后在向量数据库中进行相似度搜索，检索到的相关知识片段与原始问题一同送入LLM，确保回答的准确性和针对性。
4. **REST API**：提供简单的REST API接口，支持外部系统集成和访问问答功能。

## 依赖配置

项目依赖通过Maven管理，主要依赖包括：

- Spring Boot Web
- Spring AI OpenAI集成
- Lombok（用于简化代码）
- LangChain4J ChromaDB客户端

## 启动和运行

1. 确保已安装JDK 21和Maven。
2. 克隆项目到本地。
3. 在项目根目录下运行 `mvn clean package` 构建项目。
4. 运行 `java -cp target/mini-chat-1.0-SNAPSHOT.jar org.farsight.Main` 启动应用。
5. 使用POST请求访问 `/api/qa/ask` 接口进行问答测试。

## 项目特色

- **RAG架构**：结合向量数据库和LLM，实现精准的领域知识问答。
- **模块化设计**：各功能模块解耦，便于维护和扩展。
- **Spring生态集成**：充分利用Spring Boot和Spring AI的特性，实现高效开发和配置管理。

## 未来扩展

- 支持更多文档格式（如Word、Excel等）。
- 集成其他LLM（如Google Gemini）。
- 实现更复杂的提示词工程和模型调优。
- 增加用户界面，提供更友好的交互体验。