package services.operacion.archivos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetArchivoByUuid extends RouteBuilder {

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
        
        from("direct:getArchivoByUuid").routeId("getArchivoByUuid") 
                .log("Descargando archivo ${header.uuid} desde SFTP")
                
                // Descargar archivo usando JSch directamente
                .process(exchange -> {
                    String uuid = exchange.getIn().getHeader("uuid", String.class);
                    String remoteFile = "/" + workingDir + "/" + uuid;
                    
                    log.info("Conectando a SFTP: " + username + "@" + host + ":" + port);
                    log.info("Archivo remoto: " + remoteFile);
                    
                    JSch jsch = new JSch();
                    Session session = null;
                    ChannelSftp channelSftp = null;
                    
                    try {
                        session = jsch.getSession(username, host, port);
                        session.setPassword(password);
                        session.setConfig("StrictHostKeyChecking", "no");
                        session.connect(30000);
                        
                        channelSftp = (ChannelSftp) session.openChannel("sftp");
                        channelSftp.connect(30000);
                        
                        log.info("Conectado a SFTP, verificando archivo...");
                        
                        // Verificar que el archivo existe
                        try {
                            channelSftp.lstat(remoteFile);
                            log.info("Archivo encontrado, descargando...");
                        } catch (SftpException e) {
                            throw new RuntimeException("Archivo no encontrado en SFTP: " + remoteFile + " - " + e.getMessage());
                        }
                        
                        try (InputStream inputStream = channelSftp.get(remoteFile);
                             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                            
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            
                            byte[] fileBytes = outputStream.toByteArray();
                            log.info("Archivo descargado: " + fileBytes.length + " bytes");
                            
                            exchange.getIn().setBody(fileBytes);
                        }
                        
                    } catch (JSchException | IOException | RuntimeException e) {
                        log.error("Error descargando archivo desde SFTP: " + e.getMessage(), e);
                        throw new RuntimeException("Error descargando archivo: " + e.getMessage(), e);
                    } finally {
                        if (channelSftp != null && channelSftp.isConnected()) {
                            channelSftp.disconnect();
                        }
                        if (session != null && session.isConnected()) {
                            session.disconnect();
                        }
                    }
                })
                
                // Validar que el archivo se descargó
                .choice()
                    .when(body().isNull())
                        .log("ERROR: No se pudo descargar el archivo ${header.uuid}")
                        .process(exchange -> {
                            String uuid = exchange.getIn().getHeader("uuid", String.class);
                            throw new RuntimeException("Archivo no encontrado en SFTP: " + uuid);
                        })
                    .otherwise()
                        // Comprimir con GZIP
                        .process(exchange -> {
                            try {
                                byte[] originalBytes = exchange.getIn().getBody(byte[].class);
                                
                                if (originalBytes == null || originalBytes.length == 0) {
                                    throw new RuntimeException("El archivo descargado está vacío");
                                }
                                
                                log.info("Tamaño original del archivo: " + originalBytes.length + " bytes");
                                
                                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
                                    
                                    gzipOut.write(originalBytes);
                                    gzipOut.finish();
                                    
                                    byte[] compressedBytes = baos.toByteArray();
                                    
                                    log.info("Tamaño comprimido: " + compressedBytes.length + " bytes");
                                    
                                    // Codificar en Base64
                                    String base64Encoded = Base64.getEncoder().encodeToString(compressedBytes);
                                     
                                    // Establecer el body con el contenido codificado
                                    exchange.getIn().setBody(base64Encoded);
                                }
                            } catch (java.io.IOException e) {
                                throw new RuntimeException("Error comprimiendo archivo", e);
                            }
                        })
                .end()
                .to("direct:success");
    }

}
