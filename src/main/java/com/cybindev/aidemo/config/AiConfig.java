package com.cybindev.aidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración común a todos los ejemplos.
 *
 * Spring AI autoconfigura un {@link ChatClient.Builder} a partir del ChatModel
 * disponible (aquí: Claude vía spring-ai-starter-model-anthropic). A partir de él
 * creamos un ChatClient con valores por defecto compartidos.
 */
@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        Eres un asistente útil para una demo de Spring AI.
                        Responde siempre en español, de forma clara y concisa.
                        """)
                // Registra en DEBUG cada petición/respuesta (ver application.yml)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
