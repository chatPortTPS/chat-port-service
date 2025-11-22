package com.chat.port.jms;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class JmsDocxtract extends RouteBuilder {

    @Override
    public void configure() throws Exception {
         
        from("direct:jmsSendDocxtract")
            .doTry()
                .log("Preparando mensaje para enviar a Docxtract via JMS...")
                .to("jms:queue:tps-gestor-documental-movimientos")
                .log("Mensaje enviado a Docxtract via JMS.")
            .endDoTry()
            .doCatch(Exception.class)
                .log("Error al enviar mensaje a Docxtract via JMS: ${exception.message}") 
            .end();
 
    }

}
