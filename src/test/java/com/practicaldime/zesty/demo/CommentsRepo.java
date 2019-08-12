package com.practicaldime.zesty.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class CommentsRepo {

    static final String comments = "{\n" +
            "  \"comments\": [\n" +
            "    {\"author\":  \"user1\", \"content\":  \"content-1\"},\n" +
            "    {\"author\":  \"user2\", \"content\":  \"content-2\"},\n" +
            "    {\"author\":  \"user3\", \"content\":  \"content-3\"},\n" +
            "    {\"author\":  \"user4\", \"content\":  \"content-4\"},\n" +
            "    {\"author\":  \"user5\", \"content\":  \"content-5\"}\n" +
            "  ]\n" +
            "}";

    public static Map<String, Object> comments() {
        try {
            return new ObjectMapper().readValue(comments, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
