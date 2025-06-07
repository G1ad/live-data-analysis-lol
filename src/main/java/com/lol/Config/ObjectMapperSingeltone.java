package com.lol.Config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingeltone {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    

    private ObjectMapperSingeltone() {
        // private constructor to prevent instantiation
    }

    public static ObjectMapper getInstance() {
        return objectMapper;
    }
}

