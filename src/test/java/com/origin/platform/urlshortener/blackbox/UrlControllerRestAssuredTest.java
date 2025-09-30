package com.origin.platform.urlshortener.blackbox;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"/db/cleanup.sql", "/db/url_mapping_testdata.sql"})
//@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/cleanup.sql")
public class UrlControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
    }

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE access_log");
        jdbcTemplate.execute("TRUNCATE TABLE url_mapping");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    void whenValidShortenUrlRequest_thenReturnShortUrl() {
        String originalUrl = "https://example.com";
        String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/urls")
                .then()
                .statusCode(200)
                .body("originalUrl", equalTo(originalUrl))
                .body("shortUrl", not(emptyString()));
    }

    @Test
    void whenRedirectRequest_thenReturnLocationHeader() {
        // First, create a short URL
        String originalUrl = "https://springdoc.org/";
        String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";
        String code =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/urls")
                        .then()
                        .statusCode(200)
                        .extract().path("shortUrl").toString().replaceAll(".*/", "");

        given()
                .port(port)
                .when()
                .get("/urls/" + code + "/redirect")
                .then()
                .statusCode(200);

        given().port(port).when().get("/urls/" + code).then()
                .log().body().statusCode(200)
                .assertThat().body("hitCount", equalTo(1));
    }

    @Test
    void whenInvalidShortenUrlRequest_thenReturnValidationError() {
        String invalidUrl = "not-a-url";
        String requestBody = "{\"originalUrl\": \"" + invalidUrl + "\"}";

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/urls")
                .then()
                .statusCode(400)
                .body("message", containsString("Validation failed"));
    }

    @Test
    void whenGetUrlInfoWithValidAndInvalidCode_thenReturn200Or4xx() {
        // Create a short URL
        String originalUrl = "https://example.com/info";
        String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";
        String code =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/urls")
                        .then()
                        .statusCode(200)
                        .extract().path("shortUrl").toString().replaceAll(".*/", "");

        // Valid code
        given()
                .port(port)
                .when()
                .get("/urls/" + code)
                .then()
                .statusCode(200)
                .body("_links.accessLogs.href", containsString("/urls/" + code + "/access-logs"));

        // Invalid code
        given()
                .port(port)
                .when()
                .get("/urls/invalidcode")
                .then()
                .statusCode(greaterThanOrEqualTo(400)); // 404 or 400
    }

    @Test
    void whenGetAccessLogs_thenReturn200() {
        // Create a short URL
        String originalUrl = "https://example.com/logs";
        String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";
        String code =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/urls")
                        .then()
                        .statusCode(200)
                        .extract().path("shortUrl").toString().replaceAll(".*/", "");

        given()
                .port(port)
                .when()
                .get("/urls/" + code + "/access-logs")
                .then()
                .statusCode(200)
                .body("hasMore", notNullValue());
    }
}
