package com.fooddelivery.agentservice.entity;



import java.util.List;

public class MenuPageResponse {

    private List<MenuItemDto> content;

    public MenuPageResponse() {
    }

    public List<MenuItemDto> getContent() {
        return content;
    }

    public void setContent(List<MenuItemDto> content) {
        this.content = content;
    }
}