package com.practicaldime.router.core.handler;

public interface BodyReader<T> {

    T transform(String type, byte[] bytes);
}
