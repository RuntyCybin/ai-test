package com.cybindev.aidemo.streaming;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Ejemplo 2 — Streaming.
 *
 * La respuesta se envía token a token como Server-Sent Events, ideal para UIs de chat.
 * Prueba: curl -N "http://localhost:8080/api/stream?message=Escribe un poema sobre Spring"
 */
@RestController
public class StreamingController {

    private final ChatClient chatClient;

    public StreamingController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping(value = "/api/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(defaultValue = "Escribe un poema corto sobre Spring Boot") String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
