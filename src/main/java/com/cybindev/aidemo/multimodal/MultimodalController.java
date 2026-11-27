package com.cybindev.aidemo.multimodal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Ejemplo 8 — Multimodalidad (imágenes de entrada).
 *
 * Se adjunta la imagen como Media junto al texto del usuario.
 * Prueba:
 *   curl -F "image=@foto.jpg" -F "question=¿Qué hay en esta imagen?" http://localhost:8080/api/vision
 */
@RestController
@RequestMapping("/api/vision")
public class MultimodalController {

    private final ChatClient chatClient;

    public MultimodalController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping
    public String describe(@RequestPart("image") MultipartFile image,
                           @RequestParam(defaultValue = "Describe esta imagen en detalle") String question) {
        MimeType mimeType = image.getContentType() != null
                ? MimeTypeUtils.parseMimeType(image.getContentType())
                : MimeTypeUtils.IMAGE_PNG;

        return chatClient.prompt()
                .user(u -> u.text(question).media(mimeType, image.getResource()))
                .call()
                .content();
    }
}
