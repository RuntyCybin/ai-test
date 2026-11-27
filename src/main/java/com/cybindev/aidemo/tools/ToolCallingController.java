package com.cybindev.aidemo.tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ejemplo 6 — Tool calling (function calling).
 *
 * Los métodos anotados con @Tool se exponen al modelo. Claude decide cuándo llamarlos,
 * Spring AI ejecuta el método Java y devuelve el resultado al modelo automáticamente,
 * repitiendo el ciclo hasta obtener la respuesta final.
 *
 * Prueba:
 *   /api/tools?message=¿Qué hora es ahora en Tokio y en Madrid?
 *   /api/tools?message=¿En qué estado está el pedido A-101? Cancélalo, por favor
 */
@RestController
@RequestMapping("/api/tools")
public class ToolCallingController {

    private final ChatClient chatClient;
    private final DateTimeTools dateTimeTools;
    private final OrderTools orderTools;

    public ToolCallingController(ChatClient chatClient, DateTimeTools dateTimeTools, OrderTools orderTools) {
        this.chatClient = chatClient;
        this.dateTimeTools = dateTimeTools;
        this.orderTools = orderTools;
    }

    @GetMapping
    public String chat(@RequestParam(defaultValue = "¿Qué hora es en Tokio?") String message) {
        return chatClient.prompt()
                .system("Eres el asistente de atención al cliente de una tienda online. "
                        + "Usa las herramientas disponibles; no inventes datos de pedidos.")
                .user(message)
                .tools(dateTimeTools, orderTools)
                .call()
                .content();
    }
}
