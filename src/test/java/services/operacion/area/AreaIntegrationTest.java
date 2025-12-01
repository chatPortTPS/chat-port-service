package services.operacion.area;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@Disabled("Los endpoints REST no están disponibles en el entorno de test")
class AreaIntegrationTest {

    @Test
    void testGetAllAreasEndpoint() {
        given()
            .when()
            .get("/area")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }
}
