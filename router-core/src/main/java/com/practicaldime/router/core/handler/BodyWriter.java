package com.practicaldime.router.core.handler;

public interface BodyWriter<T> {

    byte[] transform(T object);
}
