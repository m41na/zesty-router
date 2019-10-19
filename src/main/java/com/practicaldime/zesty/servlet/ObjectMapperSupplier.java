package com.practicaldime.zesty.servlet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.function.Supplier;

public class ObjectMapperSupplier implements Supplier<ObjectMapper> {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .optionalStart()
            .appendLiteral('T')
            .appendOptional(DateTimeFormatter.ISO_TIME)
            .toFormatter();
    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapperSupplier() {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sssZ"));
    }

    @Override
    public ObjectMapper get() {
        return this.mapper;
    }

    public static class LocalDateSerializer extends StdSerializer<LocalDate> {

        public LocalDateSerializer() {
            super(LocalDate.class);
        }

        @Override
        public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    public static class LocalDateDeserializer extends StdDeserializer<LocalDate> {

        protected LocalDateDeserializer() {
            super(LocalDate.class);
        }

        @Override
        public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return LocalDate.parse(parser.readValueAs(String.class));
        }
    }
}
