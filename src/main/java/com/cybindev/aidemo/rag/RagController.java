package com.cybindev.aidemo.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Ejemplo 7 — RAG (Retrieval Augmented Generation).
 *
 * El QuestionAnswerAdvisor busca en el vector store los fragmentos más parecidos a la
 * pregunta y los inyecta en el prompt, de modo que Claude responde con información
 * privada que no conoce (resources/docs/politica-empresa.md).
 *
 * Prueba:
 *   /api/rag?question=¿Cuántos días de teletrabajo tengo a la semana?
 *   /api/rag/search?query=vacaciones   (solo la búsqueda semántica, sin LLM)
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagController(ChatClient chatClient, @Lazy VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping
    public String ask(@RequestParam(defaultValue = "¿Cuántos días de vacaciones tengo?") String question) {
        return chatClient.prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(3).similarityThreshold(0.3).build())
                        .build())
                .user(question)
                .call()
                .content();
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(3).build())
                .stream()
                .map(Document::getText)
                .toList();
    }
}
