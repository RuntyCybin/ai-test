package com.cybindev.aidemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** Comprueba que el contexto arranca (no llama a la API de Anthropic). */
@SpringBootTest(properties = "spring.ai.anthropic.api-key=test-key")
class AidemoApplicationTests {

    @Test
    void contextLoads() {
    }
}
