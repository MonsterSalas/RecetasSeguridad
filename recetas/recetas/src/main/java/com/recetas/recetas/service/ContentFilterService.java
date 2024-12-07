package com.recetas.recetas.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;



@Service
public class ContentFilterService {
    private final Set<String> bannedWords = new HashSet<>(Arrays.asList(
        "insulto", "palabrota", "ofensivo","puta","te amo", "Un dia tendremos hijos","Nos vamos a casar","Para siempre juntos"  // Add banned words
    ));
    
    public boolean containsOffensiveContent(String content) {
        return bannedWords.stream()
            .anyMatch(word -> content.toLowerCase().contains(word.toLowerCase()));
    }
    
    public String filterContent(String content) {
        String filteredContent = content;
        for (String word : bannedWords) {
            String stars = "*".repeat(word.length());
            filteredContent = filteredContent.replaceAll("(?i)" + word, stars);
        }
        return filteredContent;
    }
}