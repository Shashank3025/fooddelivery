package com.fooddelivery.agentservice.entity;


public class AiAgentRequest {

    private Long userId;
    private String message;

    public AiAgentRequest() {
    }

    public AiAgentRequest(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}