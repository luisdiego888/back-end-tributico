package com.project.demo.logic.entity.chatbot;

import java.util.UUID;

public class ChatbotReponse {
    private UUID chatId;
    private String answer;

    public ChatbotReponse(UUID chatId, String answer) {
        this.chatId = chatId;
        this.answer = answer;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
