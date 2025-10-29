package services.operacion.archivos;

import java.util.Base64;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ElasticConector extends RouteBuilder {

    @ConfigProperty(name = "elasticsearch.host", defaultValue = "")
    String hostElastic;

    @ConfigProperty(name = "elasticsearch.user", defaultValue = "")
    String userElastic;

    @ConfigProperty(name = "elasticsearch.password", defaultValue = "")
    String passwordElastic;

    @ConfigProperty(name = "elastic.index.name", defaultValue = "")
    String elasticIndexName;

    @Override
    public void configure() throws Exception {

        // Crear SSLContext que confía en todos los certificados
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Crear HttpClientConfigurer que usa el SSLContext personalizado
        HttpClientConfigurer configurer = (HttpClientBuilder httpClientBuilder) -> {
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE
            );

            httpClientBuilder.setConnectionManager(
                PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build()
            );
        };

        getCamelContext().getRegistry().bind("myHttpClientConfigurer", configurer);

        String auth = "Basic " + Base64.getEncoder()
            .encodeToString((userElastic + ":" + passwordElastic).getBytes(java.nio.charset.StandardCharsets.UTF_8));

        from("direct:callElasticSearchHttp")  // tu consumer
            // Limpia cualquier arrastre del request entrante
            .removeHeaders("CamelHttp*")
            .removeHeader(org.apache.camel.Exchange.HTTP_PATH)
            .removeHeader(org.apache.camel.Exchange.HTTP_URI)
            .removeHeader(org.apache.camel.Exchange.HTTP_QUERY)

            // Configura la llamada a ES
            .setHeader("Authorization", constant(auth))
            .setHeader(org.apache.camel.Exchange.HTTP_METHOD, constant("POST"))
            .setHeader(org.apache.camel.Exchange.CONTENT_TYPE, constant("application/json"))

            // Fuerza el path exacto hacia Elasticsearch
            .setHeader(org.apache.camel.Exchange.HTTP_PATH, constant("/" + elasticIndexName + "/_search"))

            .log(LoggingLevel.ERROR, "Consulta a ElasticSearch: ${body}")
            // Usa endpoint sin path; bridgeEndpoint para no reenviar Host, etc.
            .to(hostElastic + "?bridgeEndpoint=true&httpClientConfigurer=#myHttpClientConfigurer")

            .log(org.apache.camel.LoggingLevel.ERROR, "Respuesta de ElasticSearch: ${body}");




    }

}
