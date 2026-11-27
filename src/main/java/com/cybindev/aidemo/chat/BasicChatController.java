package com.cybindev.aidemo.chat;

import com.anthropic.models.messages.OutputConfig;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ejemplo 1 — Chat básico con ChatClient.
 *
 * GET /api/chat?message=...           → solo el texto
 * GET /api/chat/details?message=...   → texto + metadatos (modelo, tokens)
 * GET /api/chat/deep?message=...      → opciones por petición (más esfuerzo de razonamiento)
 */
@RestController
@RequestMapping("/api/chat")
public class BasicChatController {

    private final ChatClient chatClient;

    public BasicChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public String chat(@RequestParam(defaultValue = "Cuéntame un dato curioso sobre Java") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/details")
    public ChatDetails details(@RequestParam(defaultValue = "¿Qué es Spring AI?") String message) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();

        Usage usage = response.getMetadata().getUsage();
        return new ChatDetails(
                response.getResult().getOutput().getText(),
                response.getMetadata().getModel(),
                usage.getPromptTokens(),
                usage.getCompletionTokens());
    }

    /**
     * Las opciones específicas del proveedor se pueden fijar por petición.
     * En Claude, "effort" controla cuánto razona el modelo (low … max).
     */
    @GetMapping("/deep")
    public String deep(@RequestParam(defaultValue = "Diseña un algoritmo para detectar ciclos en un grafo dirigido y explica su complejidad") String message) {
        return chatClient.prompt()
                .user(message)
                .options(AnthropicChatOptions.builder().effort(OutputConfig.Effort.HIGH))
                .call()
                .content();
    }

    public record ChatDetails(String answer, String model, Integer inputTokens, Integer outputTokens) {
    }
}
