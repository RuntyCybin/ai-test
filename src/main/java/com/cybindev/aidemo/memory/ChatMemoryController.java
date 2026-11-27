package com.cybindev.aidemo.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Ejemplo 5 — Memoria de conversación.
 *
 * Los LLM no tienen estado: el MessageChatMemoryAdvisor guarda los mensajes de cada
 * conversación (identificada por conversationId) y los reenvía en cada petición.
 * Spring AI autoconfigura un ChatMemory (MessageWindowChatMemory en memoria, últimos 20
 * mensajes); para persistirlo basta con añadir p. ej. spring-ai-model-chat-memory-repository-jdbc.
 *
 * Prueba:
 *   POST /api/memory/ana   "Hola, me llamo Ana y programo en Kotlin"
 *   POST /api/memory/ana   "¿Cómo me llamo y qué lenguaje uso?"
 */
@RestController
@RequestMapping("/api/memory")
public class ChatMemoryController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatMemoryController(ChatClient chatClient, ChatMemory chatMemory) {
        // mutate() crea una copia del cliente común a la que añadimos el advisor de memoria
        this.chatClient = chatClient.mutate()
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.chatMemory = chatMemory;
    }

    @PostMapping("/{conversationId}")
    public String chat(@PathVariable String conversationId, @RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    @GetMapping("/{conversationId}")
    public List<String> history(@PathVariable String conversationId) {
        return chatMemory.get(conversationId).stream()
                .map(m -> m.getMessageType() + ": " + m.getText())
                .toList();
    }

    @DeleteMapping("/{conversationId}")
    public void clear(@PathVariable String conversationId) {
        chatMemory.clear(conversationId);
    }
}
