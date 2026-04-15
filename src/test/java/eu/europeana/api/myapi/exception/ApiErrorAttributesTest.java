package eu.europeana.api.myapi.exception;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getLogger(ApiErrorAttributesTest.class);

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     *  Test if 404s return a json-formatted response (and not default Spring Boot whitelist error)
     */
    @Test
    public void test404Json() {
        String path = "/not-exists";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        assertEquals("404", response.getString("status"));
    }

    /**
     * Test Europeana API exception handled by our MyGlobalExceptionHandler (catch and let Spring Boot generate error)
     * By default we should have no stacktrace or message field
     */
    @Test
    public void testError3FieldsNoStacktraceNoMessage() {
        String path = "/error3?param1=value1";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertFalse(response.getBoolean("success"));
        assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getInt("status"));
        assertEquals("I'm a teapot", response.getString("error"));
        assertEquals("MyErrorCode", response.getString("code"));
        assertNull(response.getString("message")); // null because we set message to be on_param in test config
        assertEquals("https://www.test.com", response.getString("seeAlso"));
        assertNull(response.getString("trace")); // null because we set trace to be on_param in test config
        assertTrue(response.getString("timestamp").contains("T"));
        // TODO INVESTIGATE in Spring Boot 3 the path part is always null!?
        //assertEquals(path, response.getString("path"));

    }

    /**
     * Test Europeana API exception handled by our MyGlobalExceptionHandler (catch and let Spring Boot generate error)
     * Check if we get a trace field with stacktrace when profile=debug parameter is added
     */
    @Test
    public void testError3WithStacktrace() {
        String path = "/error3?param1=value1&profile=test+debug";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertTrue(StringUtils.hasLength(response.getString("trace")));
    }

    /**
     * Test Europaena API exception handled by our MyGlobalExceptionHandler (catch and let Spring Boot generate error)
     * Check if we get a message field when the message parameter is added
     */
    @Test
    public void testError3WithMessage() {
        String path = "/error3?param1=value1&message";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertFalse(response.getString("message").isEmpty());
    }

    /**
     * Test Europeana API exception handled by API commons global exception handler
     * By default we should have no stacktrace or message field
     */
    @Test
    public void testError2FieldsNoStacktraceNoMessage() {
        String path = "/error2?param1=value1";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertFalse(response.getBoolean("success"));
        assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), response.getInt("status"));
        assertEquals("Not Acceptable", response.getString("error"));
        assertEquals("BasicErrorCode", response.getString("code"));
        // TODO FIX API commons implementation currently always shows message field AND sets it's own seeAlso!? but only for this error?
        //assertNull(response.getString("message")); // null because we set message to be on_param in test config
        //assertEquals("https://www.test.com", response.getString("seeAlso"));
        assertNull(response.getString("trace")); // null because we set trace to be on_param in test config
        assertTrue(response.getString("timestamp").contains("T"));
        // TODO INVESTIGATE in Spring Boot 3 the path part is always null!?
        //assertEquals(path, response.getString("path"));

    }

    /**
     * Test Europeana API exception handled by API commons global exception handler
     * Check if we get a trace field with stacktrace when profile=debug parameter is added
     */
    @Test
    public void testError2WithStacktrace() {
        String path = "/error2?param1=value1&profile=test+debug";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        // TODO FIX not working right now
        //assertTrue(StringUtils.hasLength(response.getString("trace")));
    }

    /**
     * Test Europeana API exception handled by API commons global exception handler
     * Check if we get a message field when the message parameter is added
     */
    @Test
    public void testError2WithMessage() {
        String path = "/error2?param1=value1&message";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertFalse(response.getString("message").isEmpty());
    }

    /**
     * Test runtime exception handled by API commons global exception handler
     * By default we should have no stacktrace or message field
     */
    @Test
    public void testError1FieldsNoStacktraceNoMessage() {
        String path = "/error1?param1=value1";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertFalse(response.getBoolean("success"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getInt("status"));
        assertEquals("Internal Server Error", response.getString("error"));
        assertNull( response.getString("code"));
        assertNull(response.getString("message")); // null because we set message to be on_param in test config
        assertEquals("https://www.test.com", response.getString("seeAlso"));
        assertNull(response.getString("trace")); // null because we set trace to be on_param in test config
        assertTrue(response.getString("timestamp").contains("T"));
        // TODO INVESTIGATE in Spring Boot 3 the path part is always null!?
        //assertEquals(path, response.getString("path"));

    }

    /**
     * Test runtime exception handled by API commons global exception handler
     * Check if we get a trace field with stacktrace when profile=debug parameter is added
     */
    @Test
    public void testError1WithStacktrace() {
        String path = "/error1?param1=value1&profile=test+debug";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertTrue(StringUtils.hasLength(response.getString("trace")));
    }

    /**
     * Test runtime exception handled by API commons global exception handler
     * Check if we get a message field when the message parameter is added
     */
    @Test
    public void testError1WithMessage() {
        String path = "/error1?param1=value1&message";
        JsonPath response = given().
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).get(path).
                then().contentType(ContentType.JSON).extract().response().jsonPath();

        LOG.debug("Response: {}", response.prettyPrint());

        assertFalse(response.getString("message").isEmpty());
    }
}
