package com.recetas.recetas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/home").setViewName("home");
        // No agregamos el mapping para index porque lo manejamos en el controller
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar manejador para archivos subidos
        registry.addResourceHandler("/uploads/**")
               .addResourceLocations("file:./uploads/");
        
        // Recursos estáticos
        registry.addResourceHandler("/static/**")
               .addResourceLocations("classpath:/static/");
        
        // Imágenes
        registry.addResourceHandler("/images/**")
               .addResourceLocations("classpath:/static/images/");
    }
}