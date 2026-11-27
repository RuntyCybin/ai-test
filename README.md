# aidemo — Ejemplos de Spring AI

Proyecto de ejemplo con las funcionalidades principales de **Spring AI 2.0** sobre
**Spring Boot 4.1**, usando **Claude** (Anthropic, modelo `claude-opus-5-5`) como modelo de chat.

## Requisitos

- Java 21 o superior y Maven 3.9 o superior
- Una API key de Anthropic

```bash
export ANTHROPIC_API_KEY=sk-ant-...
mvn spring-boot:run
```

Las peticiones de ejemplo están en [requests.http](requests.http) (se pueden lanzar desde
VS Code con la extensión *REST Client* o desde IntelliJ).

## Ejemplos

| # | Concepto | Código | Endpoint |
|---|----------|--------|----------|
| 1 | `ChatClient` básico, metadatos (tokens) y opciones por petición | [chat/BasicChatController.java](src/main/java/com/cybindev/aidemo/chat/BasicChatController.java) | `GET /api/chat`, `/api/chat/details`, `/api/chat/deep` |
| 2 | Streaming con `Flux` / SSE | [streaming/StreamingController.java](src/main/java/com/cybindev/aidemo/streaming/StreamingController.java) | `GET /api/stream` |
| 3 | Prompt templates (fichero `.st` y en línea) y system prompt | [prompt/PromptTemplateController.java](src/main/java/com/cybindev/aidemo/prompt/PromptTemplateController.java) | `POST /api/prompt/translate`, `/api/prompt/explain` |
| 4 | Salida estructurada: `.entity(Record.class)`, listas y extracción | [structured/StructuredOutputController.java](src/main/java/com/cybindev/aidemo/structured/StructuredOutputController.java) | `GET /api/structured/recipe`, `/books`, `POST /extract-contact` |
| 5 | Memoria de conversación con `MessageChatMemoryAdvisor` | [memory/ChatMemoryController.java](src/main/java/com/cybindev/aidemo/memory/ChatMemoryController.java) | `POST/GET/DELETE /api/memory/{id}` |
| 6 | Tool calling con `@Tool` (lectura y modificación de estado) | [tools/](src/main/java/com/cybindev/aidemo/tools/) | `GET /api/tools` |
| 7 | RAG: ingesta, embeddings locales, `SimpleVectorStore` y `QuestionAnswerAdvisor` | [rag/](src/main/java/com/cybindev/aidemo/rag/) | `GET /api/rag`, `/api/rag/search` |
| 8 | Multimodal: imagen + texto | [multimodal/MultimodalController.java](src/main/java/com/cybindev/aidemo/multimodal/MultimodalController.java) | `POST /api/vision` (multipart) |

La configuración común (system prompt por defecto y `SimpleLoggerAdvisor` para ver las
peticiones en el log) está en [config/AiConfig.java](src/main/java/com/cybindev/aidemo/config/AiConfig.java).
La configuración del modelo está en [application.yml](src/main/resources/application.yml).

## Notas

- **Embeddings para RAG**: Anthropic no ofrece modelo de embeddings, así que se usa
  `spring-ai-starter-model-transformers` (ONNX, all-MiniLM-L6-v2, se ejecuta en local).
  La primera llamada a `/api/rag` descarga el modelo, unos 90 MB, y por eso tarda más.
  `/api/rag/search` funciona sin API key.
- **Opciones del modelo**: `claude-opus-5-5` no acepta `temperature`/`top-p`/`top-k` ni
  desactivar el razonamiento. La profundidad se controla con `effort` (`low` … `max`),
  como en `/api/chat/deep`.
- **Cambiar de proveedor**: el código solo usa la API portable `ChatClient`. Para usar
  OpenAI u Ollama basta con cambiar el starter en el `pom.xml` y las propiedades.
  Solo `BasicChatController#deep` usa opciones específicas de Anthropic.
- **VS Code / IDE**: los nombres de parámetros (`@RequestParam`, `@Tool`) requieren
  compilar con `-parameters`. Maven ya lo hace. Si el IDE da el error *"parameter name
  information not available"*, recarga el proyecto Java (*Java: Reload Projects*) para
  que aplique la configuración del `pom.xml`.
