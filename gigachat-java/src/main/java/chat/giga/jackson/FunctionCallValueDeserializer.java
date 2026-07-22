package chat.giga.jackson;

import chat.giga.model.completion.ChatFunctionCall;
import chat.giga.model.completion.ChatFunctionCallEnum;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Jackson 3 variant of {@link FunctionCallJsonDeserializer}.
 */
public class FunctionCallValueDeserializer extends ValueDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            return ChatFunctionCallEnum.fromValue(p.getString());
        } else if (p.currentToken() == JsonToken.START_OBJECT) {
            return ctxt.readValue(p, ChatFunctionCall.class);
        }

        return null;
    }
}
