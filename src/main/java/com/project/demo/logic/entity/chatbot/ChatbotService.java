package com.project.demo.logic.entity.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChatbotService {

    @Autowired
    private ChatClient chatClient;

    public ChatbotReponse chat(ChatbotRequest chatbotRequest) {
        UUID chatId = Optional.ofNullable(chatbotRequest.getChatId()).orElse(UUID.randomUUID());
        String answer = chatClient
                .prompt()
                .user(chatbotRequest.getQuestion())
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", chatId))
                .call()
                .content();
        return new ChatbotReponse(chatId, answer);
    }
}
