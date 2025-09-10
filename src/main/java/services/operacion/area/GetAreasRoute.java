package services.operacion.area;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import services.operacion.area.dto.AreaResponse;
import services.operacion.area.dto.BukApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.text.Normalizer;

@ApplicationScoped
public class GetAreasRoute extends RouteBuilder {

    @ConfigProperty(name = "buk.api.key", defaultValue = "")
    String bukApiKey;

    @ConfigProperty(name = "buk.api.url", defaultValue = "")
    String bukURL;

    @Override
    public void configure() throws Exception {
        
        // Configurar la estrategia de agregación para combinar resultados
        AggregationStrategy areasAggregationStrategy = new AggregationStrategy() {
            @Override
            @SuppressWarnings("unchecked")
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                List<AreaResponse> allAreas = new ArrayList<>();
                
                if (oldExchange != null) {
                    List<AreaResponse> oldAreas = oldExchange.getIn().getBody(List.class);
                    if (oldAreas != null) {
                        allAreas.addAll(oldAreas);
                    }
                }
                
                if (newExchange != null) {
                    List<AreaResponse> newAreas = newExchange.getIn().getBody(List.class);
                    if (newAreas != null) {
                        allAreas.addAll(newAreas);
                    }
                }
                
                if (oldExchange != null) {
                    oldExchange.getIn().setBody(allAreas);
                    return oldExchange;
                } else {
                    newExchange.getIn().setBody(allAreas);
                    return newExchange;
                }
            }
        };

        // Ruta principal
        from("direct:getAreas")
            .doTry()
                .log("Iniciando obtención de áreas desde BUK API")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("auth_token", constant(bukApiKey))
                // Primera llamada para obtener paginación 
                .toD("https://" + bukURL + "&bridgeEndpoint=true")
                .log(LoggingLevel.DEBUG, "Respuesta primera página obtenida")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String firstPageResponse = exchange.getIn().getBody(String.class);
                        ObjectMapper objectMapper = new ObjectMapper();
                        
                        // Parsear la primera respuesta
                        BukApiResponse firstPageData = objectMapper.readValue(firstPageResponse, BukApiResponse.class);
                        
                        // Extraer áreas de la primera página
                        List<AreaResponse> firstPageAreas = extractChildrenAreas(firstPageData);
                        
                        // Guardar información de paginación
                        int totalPages = 1;
                        if (firstPageData.getPagination() != null && firstPageData.getPagination().getTotalPages() != null) {
                            totalPages = firstPageData.getPagination().getTotalPages();
                        }
                        
                        exchange.setProperty("totalPages", totalPages);
                        exchange.setProperty("firstPageAreas", firstPageAreas);
                        
                        // Generar lista de URLs para páginas adicionales
                        if (totalPages > 1) {
                            List<String> additionalPageUrls = IntStream.rangeClosed(2, totalPages)
                                .mapToObj(pageNumber -> "https://" + bukURL + "&bridgeEndpoint=true&page=" + pageNumber)
                                .collect(Collectors.toList());
                            exchange.setProperty("additionalPageUrls", additionalPageUrls);
                        }
                        
                        // Establecer las áreas de la primera página como cuerpo inicial
                        exchange.getIn().setBody(firstPageAreas);
                    }
                    
                    private List<AreaResponse> extractChildrenAreas(BukApiResponse bukResponse) {
                        return bukResponse.getData()
                            .stream()
                            .filter(dataContent -> dataContent.getChildrenArea() != null)
                            .flatMap(dataContent -> dataContent.getChildrenArea().stream())
                            .map(child -> new AreaResponse(child.getId(), child.getName()))
                            .collect(Collectors.toList());
                    }
                })
            .doCatch(Exception.class) 
                .to("direct:error")
            .end()
            // Verificar si hay páginas adicionales (fuera del try-catch)
            .choice()
                .when(simple("${exchangeProperty.totalPages} > 1"))
                    .log("Obteniendo ${exchangeProperty.totalPages} páginas de forma concurrente")
                    .to("direct:processAdditionalPages")
                .otherwise()
                    .log("Solo una página encontrada, usando datos de primera página")
                    // El body ya contiene las áreas de la primera página
            .end()
            .log(LoggingLevel.ERROR, "Total de áreas children obtenidas: ${body.size}")
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    @SuppressWarnings("unchecked")
                    List<AreaResponse> areas = exchange.getIn().getBody(List.class);
                    
                    if (areas != null && !areas.isEmpty()) {
                        // Primero asegurar que todas las áreas tengan public_id generado
                        areas.forEach(area -> {
                            if (area.getPublicId() == null && area.getName() != null) {
                                String publicId = removeAccents(area.getName().trim().toLowerCase()).replaceAll("\\s+", "_");
                                area.setPublicId(publicId);
                            }
                        });
                        
                        // Eliminar duplicados por name usando stream distinctBy
                        List<AreaResponse> uniqueAreas = areas.stream()
                            .collect(Collectors.toMap(
                                AreaResponse::getPublicId, // key: public_id
                                area -> area,          // value: el objeto completo
                                (existing, replacement) -> existing // en caso de duplicado, mantener el primero
                            ))
                            .values()
                            .stream()
                            .collect(Collectors.toList());
                        
                        exchange.getIn().setBody(uniqueAreas);
                    }
                }
                
                private String removeAccents(String text) {
                    if (text == null) {
                        return null;
                    }
                    return Normalizer.normalize(text, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", ""); // Elimina marcas diacríticas (tildes, acentos)
                }
            })
            .log(LoggingLevel.ERROR, "Total de áreas únicas después de eliminar duplicados: ${body.size}")
            .setHeader("message", simple("${body.size} áreas obtenidas exitosamente"))
            .to("direct:success");

        // Ruta auxiliar para procesar páginas adicionales
        from("direct:processAdditionalPages")
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    @SuppressWarnings("unchecked")
                    List<String> urls = (List<String>) exchange.getProperty("additionalPageUrls");
                    exchange.getIn().setBody(urls);
                }
            })
            .split(body())
                .parallelProcessing()
                .aggregationStrategy(areasAggregationStrategy)
                .to("direct:fetchAdditionalPage")
            .end()
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    // Combinar con las áreas de la primera página
                    @SuppressWarnings("unchecked")
                    List<AreaResponse> firstPageAreas = (List<AreaResponse>) exchange.getProperty("firstPageAreas");
                    @SuppressWarnings("unchecked")
                    List<AreaResponse> additionalAreas = exchange.getIn().getBody(List.class);
                    
                    List<AreaResponse> allAreas = new ArrayList<>();
                    if (firstPageAreas != null) {
                        allAreas.addAll(firstPageAreas);
                    }
                    if (additionalAreas != null) {
                        allAreas.addAll(additionalAreas);
                    }
                    
                    exchange.getIn().setBody(allAreas);
                }
            });

        // Ruta auxiliar para obtener páginas adicionales
        from("direct:fetchAdditionalPage")
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("auth_token", constant(bukApiKey))
            .toD("${body}")
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String pageResponse = exchange.getIn().getBody(String.class);
                    ObjectMapper objectMapper = new ObjectMapper();
                    
                    try {
                        BukApiResponse pageData = objectMapper.readValue(pageResponse, BukApiResponse.class);
                        List<AreaResponse> pageAreas = pageData.getData()
                            .stream()
                            .filter(dataContent -> dataContent.getChildrenArea() != null)
                            .flatMap(dataContent -> dataContent.getChildrenArea().stream())
                            .map(child -> new AreaResponse(child.getId(), child.getName()))
                            .collect(Collectors.toList());
                        
                        exchange.getIn().setBody(pageAreas);
                    } catch (Exception e) {
                        // En caso de error en una página, devolver lista vacía
                        exchange.getIn().setBody(new ArrayList<AreaResponse>());
                    }
                }
            });
    }
}
