package com.recetas.recetas.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String cuisine;
    private String ingredients;
    private String country;
    private String difficulty;
    private String preparationTime;
    private String cookingTime;
    private String instructions;
    private String imageUrl;
    private boolean popular;
}