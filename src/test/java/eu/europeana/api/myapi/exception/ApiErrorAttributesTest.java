package eu.europeana.api.myapi.exception;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Test error responses
 * MockMvc does not support testing error response contents, that's why we use RestAssured here instead
 * Note that these tests depend on the error settings defined in application.yml
 */
@ActiveProfiles("test") // to load application-test.yml
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApiErrorAttributesTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     *  Test if 404s return json response (and not default Spring Boot whitelist error)
     */
    @Test
    public void test404Json() {
        String path = "/not-exists";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        assertEquals("404", response.getString("status"));
    }

    @Test
    public void testErrorFieldNoStacktraceNoMessage() {
        String path = "/myApi/dummyError?param1=value1";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        assertFalse(response.getBoolean("success"));
        assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getInt("status"));
        assertEquals("I'm a teapot", response.getString("error"));
        assertEquals("MyErrorCode", response.getString("code"));
        assertNull(response.getString("message")); // null because we set message to be on_param in test config
        assertEquals("https://www.test.com", response.getString("seeAlso"));
        assertNull(response.getString("trace")); // null because we set trace to be on_param in test config
        // Some version return timestamp as an integer instead of ISO date
        assertTrue(response.getString("timestamp").contains("T"));
        // TODO in Spring Boot 3 the path is no longer available!
        //assertEquals(path, response.getString("path"));

    }

    /**
     * Check if we get stacktraces when profile=debug parameter is added
     */
    @Test
    public void testWithStacktrace() {
        String path = "/myApi/dummyError?param1=value1&profile=test+debug";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        assertTrue(StringUtils.hasLength(response.getString("trace")));
    }

    /**
     * Check if we get stacktraces when message parameter is added
     */
    @Test
    public void testWithMessage() {
        String path = "/myApi/dummyError?param1=value1&message";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        assertFalse(response.getString("message").isEmpty());
    }

}
