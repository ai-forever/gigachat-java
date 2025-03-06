package chat.giga.util;

import chat.giga.model.Balance;
import chat.giga.model.BalanceResponse;
import chat.giga.model.Model;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCount;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.completion.ChatFunction;
import chat.giga.model.completion.ChatFunctionCall;
import chat.giga.model.completion.ChatFunctionParameters;
import chat.giga.model.completion.ChatFunctionParametersProperty;
import chat.giga.model.completion.ChatFunctionsFewShotExamples;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.Choice;
import chat.giga.model.completion.ChoiceChunk;
import chat.giga.model.completion.ChoiceFinishReason;
import chat.giga.model.completion.ChoiceMessage;
import chat.giga.model.completion.ChoiceMessageChunk;
import chat.giga.model.completion.ChoiceMessageFunctionCall;
import chat.giga.model.completion.CompletionChunkResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.completion.MessageRole;
import chat.giga.model.completion.Usage;
import chat.giga.model.embedding.Embedding;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.embedding.EmbeddingUsage;
import chat.giga.model.file.AccessPolicy;
import chat.giga.model.file.AvailableFilesResponse;
import chat.giga.model.file.FileDeletedResponse;
import chat.giga.model.file.FileResponse;
import chat.giga.model.file.UploadFileRequest;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class TestData {

    public ModelResponse modelResponse() {
        return ModelResponse.builder()
                .addData(Model.builder().id("test").object("test").ownedBy("test").build())
                .addData(Model.builder().id("test2").object("test2").ownedBy("test2").build())
                .object("test")
                .build();
    }

    public CompletionRequest completionRequest() {
        return CompletionRequest.builder()
                .model("testModel")
                .message(ChatMessage.builder()
                        .role(Role.SYSTEM)
                        .content("test")
                        .functionsStateId("testState")
                        .attachment("testAttachment")
                        .build())
                .functionCall(ChatFunctionCall.builder()
                        .name("testFunc")
                        .partialArguments(Map.of("testArg", "testVal"))
                        .build())
                .function(ChatFunction.builder()
                        .name("testFunc")
                        .description("testDescription")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .property("testProp", ChatFunctionParametersProperty.builder()
                                        .type("string")
                                        .description("testDescription")
                                        .addEnum("testEnum")
                                        .build())
                                .build())
                        .fewShotExample(ChatFunctionsFewShotExamples.builder()
                                .request("test")
                                .param("testParam", "testVal")
                                .build())
                        .returnParameters(ChatFunctionParameters.builder()
                                .type("object")
                                .property("testProp", ChatFunctionParametersProperty.builder()
                                        .type("array")
                                        .description("testDescription")
                                        .item(ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .build())
                                        .build())
                                .build())
                        .build())
                .temperature(0.5f)
                .topP(0.7f)
                .maxTokens(1)
                .repetitionPenalty(0.1f)
                .updateInterval(2)
                .build();
    }

    public CompletionResponse completionResponse() {
        return CompletionResponse.builder()
                .choice(Choice.builder()
                        .message(ChoiceMessage.builder()
                                .role(MessageRole.ASSISTANT)
                                .content("test")
                                .created(1234)
                                .name("testFunc")
                                .functionsStateId("testState")
                                .functionCall(ChoiceMessageFunctionCall.builder()
                                        .name("testFunc")
                                        .argument("testArg", "testVal")
                                        .build())
                                .build())
                        .index(0)
                        .finishReason(ChoiceFinishReason.STOP)
                        .build())
                .created(3214)
                .model("testModel")
                .usage(Usage.builder()
                        .promptTokens(1)
                        .completionTokens(2)
                        .totalTokens(3)
                        .build())
                .object("test")
                .build();
    }

    public CompletionChunkResponse completionChunkResponse() {
        return CompletionChunkResponse.builder()
                .choice(ChoiceChunk.builder()
                        .delta(ChoiceMessageChunk.builder()
                                .role(MessageRole.ASSISTANT)
                                .content("test")
                                .functionCall(ChoiceMessageFunctionCall.builder()
                                        .name("testFunc")
                                        .argument("testArg", "testVal")
                                        .build())
                                .build())
                        .index(0)
                        .finishReason(ChoiceFinishReason.STOP)
                        .build())
                .created(3214)
                .model("testModel")
                .object("test")
                .build();
    }

    public EmbeddingRequest embeddingRequest() {
        return EmbeddingRequest.builder()
                .model("Embeddings")
                .input(List.of("Расскажи о современных технологиях"))
                .build();
    }

    public EmbeddingResponse embeddingResponse() {
        return EmbeddingResponse.builder()
                .model("Embeddings")
                .object("list")
                .data(List.of(Embedding.builder()
                        .usage(EmbeddingUsage.builder()
                                .promptTokens(11)
                                .build())
                        .object("embedding")
                        .embedding(List.of())
                        .index(0)
                        .build()))
                .build();
    }

    public UploadFileRequest uploadFileRequest() {
        return UploadFileRequest.builder()
                .file(new byte[100])
                .purpose("general")
                .fileName("file.pdf")
                .mimeType("application/pdf")
                .build();
    }

    public FileResponse fileResponse() {
        return FileResponse.builder()
                .id(UUID.randomUUID())
                .object("object")
                .purpose("general")
                .fileName("file.pdf")
                .createdAt(1741011256)
                .bytes(2422467)
                .accessPolicy(AccessPolicy.PRIVATE)
                .build();
    }

    public AvailableFilesResponse availableFilesResponse() {
        return AvailableFilesResponse.builder()
                .data(List.of(FileResponse.builder()
                        .accessPolicy(AccessPolicy.PRIVATE)
                        .bytes(100)
                        .createdAt(1740942137)
                        .fileName("test")
                        .id(UUID.randomUUID())
                        .purpose("general")
                        .object("file")
                        .build()))
                .build();
    }

    public FileDeletedResponse fileDeletedResponse() {
        return FileDeletedResponse.builder()
                .deleted(true)
                .id(UUID.randomUUID())
                .build();
    }

    public TokenCountRequest tokenCountRequest() {
        return TokenCountRequest.builder()
                .model("testModel")
                .addInput("test")
                .build();
    }

    public List<TokenCount> tokenCounts() {
        return List.of(TokenCount.builder()
                .tokens(1)
                .characters(2)
                .build());
    }

    public BalanceResponse balanceResponse() {
        return BalanceResponse.builder()
                .addBalance(Balance.builder()
                        .usage("testModel")
                        .value(100)
                        .build())
                .build();
    }
}
