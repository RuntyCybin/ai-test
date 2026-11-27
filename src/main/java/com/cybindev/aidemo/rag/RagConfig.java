package com.cybindev.aidemo.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Pipeline de ingesta para RAG: leer → trocear → generar embeddings → guardar.
 *
 * - EmbeddingModel: lo autoconfigura spring-ai-starter-model-transformers (modelo ONNX
 *   all-MiniLM-L6-v2 ejecutado en local; se descarga y cachea la primera vez).
 * - VectorStore: SimpleVectorStore en memoria. En producción se cambiaría por
 *   PGVector, Redis, Qdrant, etc. sin tocar el resto del código.
 *
 * Es @Lazy para que la descarga del modelo de embeddings solo ocurra cuando se use
 * el ejemplo de RAG por primera vez.
 */
@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);

    @Value("classpath:docs/politica-empresa.md")
    private Resource companyPolicy;

    @Bean
    @Lazy
    VectorStore vectorStore(@Lazy EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        List<Document> documents = new TextReader(companyPolicy).read();
        List<Document> chunks = TokenTextSplitter.builder()
                .withChunkSize(100)
                .build()
                .apply(documents);
        store.add(chunks);

        log.info("RAG: {} fragmentos indexados en el vector store", chunks.size());
        return store;
    }
}
