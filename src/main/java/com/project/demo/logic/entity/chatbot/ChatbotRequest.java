package com.project.demo.logic.entity.chatbot;

import org.springframework.lang.Nullable;

import java.util.UUID;

public class ChatbotRequest {
    @Nullable
    private UUID chatId;
    private String question;

    @Nullable
    public UUID getChatId() {
        return chatId;
    }

    public ChatbotRequest(@Nullable UUID chatId, String question) {
        this.chatId = chatId;
        this.question = question;
    }

    public void setChatId(@Nullable UUID chatId) {
        this.chatId = chatId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
