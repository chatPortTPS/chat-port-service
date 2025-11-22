package services.operacion.archivos;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import jakarta.activation.DataHandler;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
            .process(exchange -> {
                // Debug: ver qué hay en el exchange
                log.info("Body class: {}", exchange.getIn().getBody() != null ? exchange.getIn().getBody().getClass() : "null");
                log.info("Headers: {}", exchange.getIn().getHeaders().keySet());
                
                // Intentar obtener attachments
                org.apache.camel.attachment.AttachmentMessage attMsg = exchange.getIn(org.apache.camel.attachment.AttachmentMessage.class);
                Map<String, DataHandler> attachments = attMsg.getAttachments();
                
                log.info("Attachments disponibles: {}", attachments.keySet());
                
                if (attachments.isEmpty()) {
                    throw new IllegalArgumentException("No se encontraron attachments. Keys disponibles: " + attachments.keySet());
                }
                
                // Buscar el archivo (puede venir con nombre diferente)
                DataHandler dh = null;
                String attachmentKey = null;
                
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
                String metadata = exchange.getIn().getHeader("metadata", String.class);
                
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
                exchange.getIn().setHeader("metadata", metadata);
                
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
            .setBody(simple("{ \"uuid\": \"${header.fileUuid}\", \"fileName\": \"${header.originalFileName}\", \"status\": \"uploaded\" }"))
            .setHeader("Content-Type", constant("application/json")); 
            
    }

}
