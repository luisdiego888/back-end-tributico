package com.project.demo.rest.webSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class WebSocketController {

    @MessageMapping("/test")
    @SendTo("/topic/test-responses")
    public String handleTestMessage(@Payload String message) {
        return "Respuesta del servidor: " + message.toUpperCase();
    }
}