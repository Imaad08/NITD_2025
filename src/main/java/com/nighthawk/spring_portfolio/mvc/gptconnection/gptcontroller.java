package com.nighthawk.spring_portfolio.mvc.gptconnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gpt")
public class GptController {

    @Autowired
    private Chat chatService;

    @Autowired
    private GptRepository gptRepository;

    @PostMapping("/grade")
    public String gradeCode(@RequestBody String code) {
        // Generate a unique thread ID for this grading request
        String threadId = UUID.randomUUID().toString();

        // Get GPT's score response
        String gptResponse = chatService.getGptScore(code);

        // Save the entry in the database
        GptEntry entry = new GptEntry();
        entry.setCode(code);
        entry.setGptResponse(gptResponse);
        entry.setThreadId(threadId);
        gptRepository.save(entry);

        return gptResponse;
    }
}