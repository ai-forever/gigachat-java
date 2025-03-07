package chat.giga.util;

import chat.giga.model.completion.ChatFunctionCall;
import chat.giga.model.completion.ChatFunctionCallEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class FunctionCallJsonDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            return ChatFunctionCallEnum.fromValue(p.getText());
        } else if (p.currentToken() == JsonToken.START_OBJECT) {
            return ctxt.readValue(p, ChatFunctionCall.class);
        }

        return null;
    }
}
