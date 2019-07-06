package com.practicaldime.zesty.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.function.Supplier;

public class ObjectMapperSupplier implements Supplier<ObjectMapper> {

    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapperSupplier() {
        mapper.setDateFormat(new SimpleDateFormat("MMM dd, yyyy hh:mm:ss"));
    }

    @Override
    public ObjectMapper get() {
        return this.mapper;
    }
}
