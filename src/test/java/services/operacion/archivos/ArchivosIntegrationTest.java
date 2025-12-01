package services.operacion.archivos;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@QuarkusTest
@Disabled("Los endpoints REST no están disponibles en el entorno de test")
class ArchivosIntegrationTest {

    @Test
    void testGetAllArchivosEndpoint() {
        given()
            .when()
            .get("/archivos")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    void testGetArchivoByUuid() {
        String testUuid = "test-uuid-123";
        
        given()
            .pathParam("uuid", testUuid)
            .when()
            .get("/archivos/{uuid}")
            .then()
            .statusCode(200);
    }
}
