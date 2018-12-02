package com.practicaldime.zesty.route;

public interface BodyReader<T> {
    
    T transform(String type, byte[] bytes);
}
