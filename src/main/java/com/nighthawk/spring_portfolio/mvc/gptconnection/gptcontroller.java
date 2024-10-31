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

    private static final String THREAD_ID = UUID.randomUUID().toString();

    @PostMapping("/grade")
    public String gradeCode(@RequestBody String code) {
        String threadId = THREAD_ID;


        String gptResponse = chatService.getGptScore(code);

        GptEntry entry = new GptEntry();
        entry.setCode(code);
        entry.setGptResponse(gptResponse);
        entry.setThreadId(threadId); 
        gptRepository.save(entry);

        return gptResponse;
    }
}