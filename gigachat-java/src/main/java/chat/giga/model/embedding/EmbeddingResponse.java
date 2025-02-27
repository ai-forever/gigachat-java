package chat.giga.model.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse {

    @JsonProperty
    @Default
    String object = "list";

    @JsonProperty
    @Singular("addData")
    List<Embedding> data;

    @JsonProperty
    @Default
    String model = "Embeddings";
}
