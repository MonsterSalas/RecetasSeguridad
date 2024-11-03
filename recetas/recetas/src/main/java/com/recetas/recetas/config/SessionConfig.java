package com.recetas.recetas.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.CookieSerializer;

@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("Strict");
        serializer.setCookiePath("/");
        serializer.setUseSecureCookie(true); // Para HTTPS
        serializer.setCookieName("JSESSIONID"); // Nombre por defecto
        serializer.setCookieMaxAge(3600); // 1 hora
        // En producción, establece el dominio específico
        // serializer.setDomainName("tudominio.com");
        return serializer;
    }
}