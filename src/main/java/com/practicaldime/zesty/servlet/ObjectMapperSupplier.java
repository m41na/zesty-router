package com.practicaldime.zesty.servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectMapperSupplier {

    public static Supplier<ObjectMapper> version1 = new Supplier<>() {

        private ObjectMapper mapper;

        {
            this.mapper = new ObjectMapper();
            this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            this.mapper.registerModule(new JavaTimeModule());
            this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        @Override
        public ObjectMapper get() {
            return mapper;
        }
    };

    public static Supplier<ObjectMapper> version2 = new Supplier<ObjectMapper>() {

        private ObjectMapper mapper;

        {
            this.mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sssZ"));
        }

        @Override
        public ObjectMapper get() {
            return mapper;
        }
    };

    public static Function<Consumer<ObjectMapper>, Supplier<ObjectMapper>> version3 = new Function<Consumer<ObjectMapper>, Supplier<ObjectMapper>>() {

        private ObjectMapper mapper = new ObjectMapper();

        @Override
        public Supplier<ObjectMapper> apply(Consumer<ObjectMapper> objectMapperConsumer) {
            return new Supplier<ObjectMapper>() {
                @Override
                public ObjectMapper get() {
                    objectMapperConsumer.accept(mapper);
                    return mapper;
                }
            };
        }
    };
}
