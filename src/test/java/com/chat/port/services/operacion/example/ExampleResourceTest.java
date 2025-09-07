package com.chat.port.services.operacion.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ExampleResourceTest {

    @Test
    public void testGetExampleEndpoint() {
        given()
            .when().get("/example")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("message", notNullValue())
                .body("fecha", notNullValue())
                .body("body.size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testPostExampleEndpoint() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"rut_cliente\":\"12345678-9\"}")
            .when().post("/example")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("message", notNullValue())
                .body("fecha", notNullValue())
                .body("body[0]", equalTo("12345678-9"));
    }

    @Test
    public void testPostExampleEndpointWithInvalidBody() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when().post("/example")
            .then()
                .statusCode(anyOf(is(201), is(400)))
                .contentType(ContentType.JSON);
    }
}
