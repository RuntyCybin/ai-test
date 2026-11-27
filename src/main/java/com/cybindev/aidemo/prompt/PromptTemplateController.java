package com.cybindev.aidemo.prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ejemplo 3 — Prompt templates y system prompts.
 *
 * Las plantillas usan la sintaxis {variable} (StringTemplate) y pueden vivir en
 * ficheros .st dentro de resources/prompts.
 */
@RestController
@RequestMapping("/api/prompt")
public class PromptTemplateController {

    private final ChatClient chatClient;

    @Value("classpath:prompts/translator-system.st")
    private Resource translatorSystemPrompt;

    public PromptTemplateController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /** Plantilla de system prompt cargada desde fichero, con parámetros. */
    @PostMapping("/translate")
    public String translate(@RequestBody TranslationRequest request) {
        return chatClient.prompt()
                .system(s -> s.text(translatorSystemPrompt)
                        .param("language", request.language())
                        .param("tone", request.tone() == null ? "neutro" : request.tone()))
                .user(request.text())
                .call()
                .content();
    }

    /** Plantilla de user prompt definida en línea. */
    @PostMapping("/explain")
    public String explain(@RequestBody ExplainRequest request) {
        return chatClient.prompt()
                .user(u -> u.text("""
                                Explica el concepto "{concept}" a una persona con nivel {level}.
                                Usa como máximo {sentences} frases y un ejemplo.
                                """)
                        .param("concept", request.concept())
                        .param("level", request.level())
                        .param("sentences", 4))
                .call()
                .content();
    }

    public record TranslationRequest(String text, String language, String tone) {
    }

    public record ExplainRequest(String concept, String level) {
    }
}
