package org.farsight.rag.config; // 根据你的实际包名修改

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component // 确保 Spring 能够发现并注册这个 Bean
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        // 打印请求信息
        System.out.println("\n--- ChromaDB Request ---");
        System.out.println("URI: " + request.getURI());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Headers: " + request.getHeaders());
        System.out.println("Body: " + new String(body, StandardCharsets.UTF_8));
        System.out.println("------------------------");

        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
            // 打印响应信息
            System.out.println("--- ChromaDB Response ---");
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Headers: " + response.getHeaders());

            // 注意：response.getBody() 只能读取一次。
            // 为了安全地打印和后续处理，我们先将其读入字符串。
            String responseBody = "";
            if (response.getBody() != null) {
                try {
                    responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    responseBody = "[Error reading response body: " + e.getMessage() + "]";
                }
            }
            System.out.println("Body: " + responseBody);
            System.out.println("-------------------------");

            // 返回一个包装了原始响应体的新响应，以便后续代码可以再次读取
            // 这对于 Spring AI 内部处理响应体很重要
            return new BufferingClientHttpResponseWrapper(response, responseBody.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            System.err.println("Error during HTTP request to ChromaDB: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常，让 Spring 的错误处理机制介入
        }
    }
}