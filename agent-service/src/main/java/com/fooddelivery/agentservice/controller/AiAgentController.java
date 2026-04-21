
package com.fooddelivery.agentservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.agentservice.entity.AiAgentRequest;
import com.fooddelivery.agentservice.entity.AiAgentResponse;
import com.fooddelivery.agentservice.service.AiAgentService;

@RestController
@RequestMapping("/ai-agent")
public class AiAgentController {

    private final AiAgentService aiAgentService;

    public AiAgentController(AiAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @PostMapping("/chat")
    public AiAgentResponse chat(@RequestBody AiAgentRequest request) {
        return aiAgentService.chat(request.getUserId(), request.getMessage());
    }
}