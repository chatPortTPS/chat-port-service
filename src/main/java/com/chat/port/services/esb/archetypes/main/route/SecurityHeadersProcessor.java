package com.chat.port.services.esb.archetypes.main.route;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecurityHeadersProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // Obtener headers de la respuesta 
        exchange.getMessage().removeHeaders("*");

        var headers = exchange.getMessage().getHeaders();
        
        // Correlación de request - generar si no existe
        String reqId = exchange.getIn().getHeader("X-Request-ID", String.class);
        if (reqId == null || reqId.isBlank()) {
            reqId = UUID.randomUUID().toString();
        }
        headers.put("X-Request-ID", reqId);

        // Headers de seguridad
        headers.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        headers.put("X-Frame-Options", "DENY");
        headers.put("X-Content-Type-Options", "nosniff");
        headers.put("Content-Security-Policy", "default-src 'self'; img-src 'self' data:; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; object-src 'none'");
        headers.put("Referrer-Policy", "no-referrer");
        headers.put("Permissions-Policy", "geolocation=(), microphone=(), camera=(), fullscreen=(self)");
        
        // Headers adicionales para APIs REST
        headers.put("X-Content-Type-Options", "nosniff");
        headers.put("X-XSS-Protection", "1; mode=block");
        
        // Cache control para APIs (evitar cache de datos sensibles)
        headers.put("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.put("Pragma", "no-cache");
        headers.put("Expires", "0");
    }
}
