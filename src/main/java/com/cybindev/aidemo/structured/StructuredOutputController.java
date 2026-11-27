package com.cybindev.aidemo.structured;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Ejemplo 4 — Salida estructurada (Structured Output).
 *
 * Con .entity(...) Spring AI genera el JSON Schema a partir del tipo Java, lo
 * añade a la petición y convierte la respuesta del modelo en objetos.
 */
@RestController
@RequestMapping("/api/structured")
public class StructuredOutputController {

    private final ChatClient chatClient;

    public StructuredOutputController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /** Un único objeto (record). */
    @GetMapping("/recipe")
    public Recipe recipe(@RequestParam(defaultValue = "garbanzos") String ingredient) {
        return chatClient.prompt()
                .user(u -> u.text("Propón una receta sencilla cuyo ingrediente principal sea {ingredient}.")
                        .param("ingredient", ingredient))
                .call()
                .entity(Recipe.class);
    }

    /** Una lista genérica: se usa ParameterizedTypeReference. */
    @GetMapping("/books")
    public List<Book> books(@RequestParam(defaultValue = "ciencia ficción") String genre) {
        return chatClient.prompt()
                .user(u -> u.text("Recomiéndame 3 libros reales del género {genre}.")
                        .param("genre", genre))
                .call()
                .entity(new ParameterizedTypeReference<List<Book>>() {
                });
    }

    /** Extracción de datos de texto libre (caso de uso muy habitual). */
    @PostMapping("/extract-contact")
    public Contact extract(@RequestBody String freeText) {
        return chatClient.prompt()
                .system("Extrae los datos de contacto del texto. Si un dato no aparece, déjalo a null.")
                .user(freeText)
                .call()
                .entity(Contact.class);
    }

    public record Recipe(String name, int servings, int minutes, List<String> ingredients, List<String> steps) {
    }

    public record Book(String title, String author, int year, String summary) {
    }

    public record Contact(String name, String email, String phone, String company) {
    }
}
