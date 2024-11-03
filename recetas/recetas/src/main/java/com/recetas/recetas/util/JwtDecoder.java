package com.recetas.recetas.util;

import java.util.Base64;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtDecoder {

    public String decodeJwtPayload(String jwt) {
        try {
            String[] chunks = jwt.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(mapper.readValue(payload, Object.class));
        } catch (Exception e) {
            return "Error decodificando JWT: " + e.getMessage();
        }
    }
}