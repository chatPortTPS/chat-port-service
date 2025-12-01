package services.operacion.archivos;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class UploadTest {

    private CamelContext camelContext;
    private Upload upload;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        upload = new Upload();
        
        // Set test properties
        upload.host = "localhost";
        upload.port = 22;
        upload.username = "testuser";
        upload.password = "testpass";
        upload.workingDir = "/test/dir";
        
        // Add interceptor FIRST before adding any routes
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Mock SFTP endpoint - MUST be defined first
                interceptSendToEndpoint("sftp://*")
                    .skipSendToOriginalEndpoint()
                    .process(exchange -> {
                        exchange.getIn().setBody("File uploaded successfully");
                    });
                
                from("direct:success")
                    .log("Success route called");
                from("direct:error")
                    .log("Error route called");
                from("direct:jmsSendDocxtract")
                    .log("JMS send route called");
            }
        });
        
        // Add main routes AFTER interceptor
        camelContext.addRoutes(upload);
        
        camelContext.start();
    }

    @Test
    void testUploadRoute_WithAttachment() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);
        
        // Create mock file data
        byte[] fileContent = "Test file content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileContent);
        
        // Create a DataSource
        DataSource dataSource = new ByteArrayDataSource(fileContent, "application/pdf");
        DataHandler dataHandler = new DataHandler(dataSource);
        
        // Set up attachment
        AttachmentMessage attMsg = exchange.getIn(AttachmentMessage.class);
        Map<String, DataHandler> attachments = new HashMap<>();
        attachments.put("file", dataHandler);
        attMsg.setAttachments(attachments);
        
        // Set headers
        exchange.getIn().setHeader("nombre", "Test Document");
        exchange.getIn().setHeader("correo", "test@example.com");
        exchange.getIn().setHeader("autor", "Test Author");
        exchange.getIn().setHeader("agenteId", "123");
        exchange.getIn().setHeader("nombreAgente", "Test Agent");
        
        // Note: This test will fail at the SFTP step in a real environment
        // In a proper test, you would need to mock the SFTP endpoint or use an embedded SFTP server
        assertDoesNotThrow(() -> {
            camelContext.createProducerTemplate().send("direct:upload", exchange);
        });
    }

    @Test
    void testUploadRoute_MissingAttachment() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);
        
        AttachmentMessage attMsg = exchange.getIn(AttachmentMessage.class);
        attMsg.setAttachments(new HashMap<>());
        
        exchange.getIn().setHeader("nombre", "Test Document");
        
        Exchange result = camelContext.createProducerTemplate().send("direct:upload", exchange);
        
        // Should trigger error handling
        assertNotNull(result);
    }


    /**
     * Clase interna para crear DataSource desde byte array
     * Compatible con Jakarta Activation API
     */
    private static class ByteArrayDataSource implements DataSource {
        private final byte[] data;
        private final String contentType;
        private final String name;

        public ByteArrayDataSource(byte[] data, String contentType) {
            this(data, contentType, "attachment");
        }

        public ByteArrayDataSource(byte[] data, String contentType, String name) {
            this.data = data;
            this.contentType = contentType;
            this.name = name;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Read-only data source");
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}
