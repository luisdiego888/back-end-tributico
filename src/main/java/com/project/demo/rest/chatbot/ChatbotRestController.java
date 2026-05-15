package com.project.demo.rest.chatbot;

import com.project.demo.logic.entity.chatbot.ChatbotReponse;
import com.project.demo.logic.entity.chatbot.ChatbotRequest;
import com.project.demo.logic.entity.chatbot.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatbot")
public class ChatbotRestController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<ChatbotReponse> askChatbot(@RequestBody ChatbotRequest chatbotRequest) {
        ChatbotReponse answer = chatbotService.chat(chatbotRequest);
        return ResponseEntity.ok(answer);
    }
}
