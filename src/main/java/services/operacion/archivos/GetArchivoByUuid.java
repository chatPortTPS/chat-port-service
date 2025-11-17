package services.operacion.archivos;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

@ApplicationScoped
public class GetArchivoByUuid extends RouteBuilder {

    @ConfigProperty(name = "ftp.host")
    String host;
    
    @ConfigProperty(name = "ftp.port", defaultValue = "22")
    String port;
    
    @ConfigProperty(name = "ftp.username")
    String username;
    
    @ConfigProperty(name = "ftp.password")
    String password;
    
    @ConfigProperty(name = "ftp.working.dir")
    String workingDir;

    @Override
    public void configure() throws Exception {
        
        from("direct:getArchivoByUuid").routeId("getArchivoByUuid") 
            .doTry() 
                // Construir la URL de SFTP dinámicamente con el UUID
                .setHeader("ftpUrl", simple("sftp://" + username + "@" + host + ":" + port 
                    + workingDir + "/${header.uuid}?password=RAW(" + password + ")"
                    + "&binary=true&passiveMode=true&disconnect=true"))
                
                .log("Conectando a SFTP: ${header.ftpUrl}")
                
                // Obtener el archivo desde SFTP
                .toD("${header.ftpUrl}")
                
                // Comprimir con GZIP
                .process(exchange -> {
                    byte[] originalBytes = exchange.getIn().getBody(byte[].class);
                    
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
                        
                        gzipOut.write(originalBytes);
                        gzipOut.finish();
                        
                        byte[] compressedBytes = baos.toByteArray();
                        
                        // Codificar en Base64
                        String base64Encoded = Base64.getEncoder().encodeToString(compressedBytes);
                         
                        // Establecer el body con el contenido codificado
                        exchange.getIn().setBody(base64Encoded);
                    }
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();
    }

}
