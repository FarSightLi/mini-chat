import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

//@SpringBootTest
public class VectorTest {
    
//    @Autowired
    private VectorStore vectorStore;

    @Test
    public void test() {
        List <Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

// Add the documents
        vectorStore.add(documents);

// Retrieve documents similar to a query
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
    }

    @Test
    public void test2() {
        String host = "172.29.11.91";
        int port = 8010;
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Successfully connected to ChromaDB at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Failed to connect to ChromaDB at " + host + ":" + port + ": " + e.getMessage());
            // Log full stack trace for more details
        }
    }
}