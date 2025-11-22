package services.operacion.archivos;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import jakarta.activation.DataHandler;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.apache.camel.attachment.AttachmentMessage;
import java.util.List;
import services.operacion.area.dto.UploadResponse;

@ApplicationScoped
public class Upload extends RouteBuilder {

    @ConfigProperty(name = "ftp.host")
    String host;
    
    @ConfigProperty(name = "ftp.port", defaultValue = "22")
    int port;
    
    @ConfigProperty(name = "ftp.username")
    String username;
    
    @ConfigProperty(name = "ftp.password")
    String password;
    
    @ConfigProperty(name = "ftp.working.dir")
    String workingDir;

    @Override
    public void configure() throws Exception {
        
        from("direct:upload").routeId("uploadRoute")
            .log("Recibiendo archivo para subir...")
                .doTry()
                    .process(exchange -> {
                        // Debug: ver qué hay en el exchange
                        log.info("Body class: {}", exchange.getIn().getBody() != null ? exchange.getIn().getBody().getClass() : "null");
                        log.info("Headers: {}", exchange.getIn().getHeaders().keySet());
                        
                        // Intentar obtener attachments
                        AttachmentMessage attMsg = exchange.getIn(AttachmentMessage.class);
                        Map<String, DataHandler> attachments = attMsg.getAttachments();
                        
                        log.info("Attachments disponibles: {}", attachments.keySet());
                        
                        if (attachments.isEmpty()) {
                            throw new IllegalArgumentException("No se encontraron attachments. Keys disponibles: " + attachments.keySet());
                        }
                        
                        // Buscar el archivo (puede venir con nombre diferente)
                        DataHandler dh;
                        String attachmentKey;
                        
                        if (attachments.containsKey("file")) {
                            dh = attachments.get("file");
                            attachmentKey = "file";
                        } else {
                            // Tomar el primer attachment disponible
                            attachmentKey = attachments.keySet().iterator().next();
                            dh = attachments.get(attachmentKey);
                        }
                        
                        if (dh == null) {
                            throw new IllegalArgumentException("No se pudo obtener el archivo");
                        }
                        
                        InputStream inputStream = dh.getInputStream();
                        String originalFileName = dh.getName();
                        
                        log.info("Archivo encontrado con key: {}, nombre: {}", attachmentKey, originalFileName);
                        
                        // Obtener metadata opcional 
                        String correo = exchange.getIn().getHeader("correo", String.class);
                        String autor = exchange.getIn().getHeader("autor", String.class);
                        String agenteId = exchange.getIn().getHeader("agenteId", String.class);
                        String nombre = exchange.getIn().getHeader("nombre", String.class);
                        String nombreAgente = exchange.getIn().getHeader("nombreAgente", String.class);
                        
                        
                        // Generar UUID para el nombre del archivo (sin guiones y en mayúsculas)
                        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                        
                        // Obtener extensión del archivo original
                        String extension = "";
                        if (originalFileName != null && originalFileName.contains(".")) {
                            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }
                        
                        String newFileName = uuid + extension;
                        
                        // Guardar información en headers para usar en SFTP
                        exchange.getIn().setHeader("CamelFileName", workingDir + "/" + newFileName);
                        exchange.getIn().setHeader("originalFileName", originalFileName);
                        exchange.getIn().setHeader("fileUuid", uuid);
                        exchange.getIn().setHeader("correo", correo);
                        exchange.getIn().setHeader("autor", autor);
                        exchange.getIn().setHeader("agenteId", agenteId);
                        exchange.getIn().setHeader("nombre", nombre);
                        exchange.getIn().setHeader("nombreAgente", nombreAgente);
                        
                        // Setear el body con el InputStream del archivo
                        exchange.getIn().setBody(inputStream);
                        
                        log.info("Archivo procesado: {} -> {}", originalFileName, newFileName);
                    })
                    // Convertir InputStream a byte[] para evitar problemas con streaming
                    .process(exchange -> {
                        InputStream is = exchange.getIn().getBody(InputStream.class);
                        byte[] fileBytes = is.readAllBytes();
                        exchange.getIn().setBody(fileBytes);
                        log.info("Archivo convertido a bytes: {} bytes", fileBytes.length);
                    })
                    // Subir a SFTP - usar formato correcto para SFTP en Camel
                    .toD("sftp://" + username + "@" + host + ":" + port + "/"
                        + "?password=RAW(" + password + ")"
                        + "&fileName=${header.CamelFileName}"
                        + "&fileExist=Override"
                        + "&tempFileName=${header.CamelFileName}.tmp"
                        + "&knownHostsFile="
                        + "&strictHostKeyChecking=no"
                        + "&binary=true"
                        + "&connectTimeout=30000")
                    .log("Archivo subido exitosamente a SFTP: ${header.CamelFileName}")
                    .process(exchange -> {
                        String fileUuid = exchange.getIn().getHeader("fileUuid", String.class);
                        String originalFileName = exchange.getIn().getHeader("originalFileName", String.class);
                        String correo = exchange.getIn().getHeader("correo", String.class);
                        String autor = exchange.getIn().getHeader("autor", String.class);
                        String nombre = exchange.getIn().getHeader("nombre", String.class);
                        String agenteId = exchange.getIn().getHeader("agenteId", String.class);
                        String nombreAgente = exchange.getIn().getHeader("nombreAgente", String.class);

                        // Obtener extensión del archivo
                        String extension = "";
                        if (originalFileName != null && originalFileName.contains(".")) {
                            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }
                        String archivoConExtension = fileUuid + extension;
                        
                        // Obtener timestamp actual en formato ISO
                        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        
                        // Construir JSON de respuesta
                        String jsonResponse = String.format(
                            "{ \"datos\": [{ " +
                            "\"archivo\": \"%s\", " +
                            "\"nombre\": \"%s\", " +
                            "\"privacidad\": \"privado\", " +
                            "\"creacion\": \"%s\", " +
                            "\"actualizacion\": \"%s\", " +
                            "\"correo\": \"%s\", " +
                            "\"autor\": \"%s\", " +
                            "\"ruta\": \"%s\", " +
                            "\"areas\": [], " +
                            "\"agente\": \"%s\" " +
                            "}]}",
                            archivoConExtension,
                            nombre != null ? nombre : "",
                            timestamp,
                            timestamp,
                            correo != null ? correo : "",
                            autor != null ? autor : "",
                            "AGENTES/" + nombreAgente,
                            agenteId != null ? agenteId : ""
                        );
                         
                        exchange.getIn().setBody(jsonResponse);
                    })
                    .wireTap("direct:jmsSendDocxtract")
                    .log("Respuesta de artemis: ${body}") 
                    .process(exchange -> {
                        List<UploadResponse> respuesta = new ArrayList<>();
                        
                        UploadResponse uploadResponse = new UploadResponse();
                        uploadResponse.setUuid(exchange.getIn().getHeader("fileUuid", String.class));
                        uploadResponse.setNombre(exchange.getIn().getHeader("nombre", String.class));
                        uploadResponse.setCorreo(exchange.getIn().getHeader("correo", String.class));
                        uploadResponse.setAutor(exchange.getIn().getHeader("autor", String.class));
                        uploadResponse.setAgenteId(exchange.getIn().getHeader("agenteId", String.class));
                        uploadResponse.setNombreAgente(exchange.getIn().getHeader("nombreAgente", String.class));

                        respuesta.add(uploadResponse);

                        exchange.getIn().setBody(respuesta);
                    })
                    .setHeader("message", constant("Archivo subido exitosamente"))
                    .setHeader("BodyType", constant("com.chat.port.response.Response"))
                    .to("direct:success")
                .endDoTry()
                .doCatch(Exception.class)
                    .log("Error al subir archivo: ${exception.message}")
                    .to("direct:error")
                .end();

    }

}
