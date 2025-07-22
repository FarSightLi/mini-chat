package org.farsight.rag.controller;

import org.farsight.rag.qa.QaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qa")
public class QaController {
    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        return qaService.answerQuestion(question);
    }
}