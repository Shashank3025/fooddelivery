
package com.fooddelivery.agentservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.agentservice.entity.AgentOrderRequest;
import com.fooddelivery.agentservice.entity.AgentOrderResponse;
import com.fooddelivery.agentservice.entity.AgentResponse;
import com.fooddelivery.agentservice.service.AgentOrchestratorService;

@RestController
@RequestMapping("/agent")
public class AgentController 
{
	
	private final AgentOrchestratorService agentOrchestratorService;

    public AgentController(AgentOrchestratorService agentOrchestratorService) 
    {
        this.agentOrchestratorService = agentOrchestratorService;
    }

    @GetMapping("/health")
    public AgentResponse health() 
    {
        return agentOrchestratorService.healthCheck();
    }
    @PostMapping("/place-order")
    public AgentOrderResponse placeOrder(@RequestBody AgentOrderRequest request) {
        return agentOrchestratorService.placeOrder(request);
    }
}
