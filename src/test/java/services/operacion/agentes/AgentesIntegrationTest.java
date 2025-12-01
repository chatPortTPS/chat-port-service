package services.operacion.agentes;

import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@QuarkusTest
@Disabled("Los endpoints REST no están disponibles en el entorno de test")
class AgentesIntegrationTest {

    @Test
    void testGetAllAgentsEndpoint() {
        given()
            .when()
            .get("/agentes")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    void testCreateAgentEndpoint() {
        given()
            .queryParam("name", "Integration Test Agent")
            .queryParam("description", "Test Description")
            .queryParam("prompt", "Test Prompt")
            .queryParam("theme", "MINI")
            .queryParam("position", "BOTTOM_RIGHT")
            .queryParam("website", "https://test.com")
            .queryParam("type", "DYNAMIC")
            .queryParam("userCreate", "testuser")
            .contentType(ContentType.JSON)
            .when()
            .post("/agentes")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(notNullValue());
    }

    @Test
    void testCreateAgentWithoutRequiredParams() {
        given()
            .queryParam("name", "Test Agent")
            .contentType(ContentType.JSON)
            .when()
            .post("/agentes")
            .then()
            .statusCode(500); // Should fail validation
    }

    @Test
    void testGetCentralAgent() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .post("/agentes/central")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(notNullValue());
    }
}
