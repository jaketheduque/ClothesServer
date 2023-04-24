package me.jaketheduque.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.io.StringWriter;

public class PairSerializer extends JsonSerializer<Pair> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(Pair pair, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, pair);
        gen.writeFieldName(writer.toString());
    }
}
