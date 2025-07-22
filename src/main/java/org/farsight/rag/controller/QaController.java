package org.farsight.rag.controller;

import org.farsight.rag.service.QaService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qa")
public class QaController {
    private final ZhiPuAiChatModel zhiPuAiChatModel;
    private final QaService qaService;

    @Autowired
    public QaController(
             ZhiPuAiChatModel zhiPuAiChatModel,
                        QaService qaService) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
        this.qaService = qaService;
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        return qaService.answerQuestion(question);
    }


    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.zhiPuAiChatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var prompt = new Prompt(new UserMessage(message));
        return this.zhiPuAiChatModel.stream(prompt);
    }
}