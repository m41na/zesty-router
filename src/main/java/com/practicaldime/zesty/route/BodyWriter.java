package com.practicaldime.zesty.route;

public interface BodyWriter<T> {
    
    byte[] transform(T object);
}
