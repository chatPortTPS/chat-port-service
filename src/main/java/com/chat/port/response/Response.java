package com.chat.port.response;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;

@Data
public class Response implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String message;
    private Object body;
    private String fecha;

    public Response() {
        this.fecha = java.time.LocalDateTime.now().toString();
    }

    public Response(String message, Object body) {
        this.message = message;
        this.body = body;
        this.fecha = java.time.LocalDateTime.now().toString();
    }
 
    public void ok(Object body) {
        this.message = "Proceso exitoso";
        this.body = body; 
    }

    public void ok(String message, Object body) {
        this.message = message;
        this.body = body; 
    }

    public void error(String message) {
        this.message = message;
        this.body = new ArrayList<>(); 
    }

}
