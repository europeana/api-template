package eu.europeana.api.myapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMVC test to check if CORS is configured as desired
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test if CORS works for GET normal requests and error requests
     */
    @Test
    public void testCORSGet() throws Exception {
        // normal (200 response) request
        testNormalResponse(mockMvc.perform(get("/myApi/{someRequest}", "123test")
                .header(HttpHeaders.ORIGIN, "https://test.com")));

        // error request
       testErrorResponse(mockMvc.perform(get("/myApi/{someRequest}", "123-test")
                .header(HttpHeaders.ORIGIN, "https://test.com")));
    }

    /**
     * Test if CORS works for HEAD normal requests and error requests
     */
    @Test
    public void testCORSHead() throws Exception {
        // normal (200 response) request
        testNormalResponse(mockMvc.perform(head("/myApi/{someRequest}", "123test")
                .header(HttpHeaders.ORIGIN, "https://test.com")));

        // error request
        testErrorResponse(mockMvc.perform(head("/myApi/{someRequest}", "123-test")
                .header(HttpHeaders.ORIGIN, "https://test.com")));
    }

    /**
     * Test if CORS works for Options (Preflight) request
     */
    @Test
    public void testCORSOptions() throws Exception {
        // typical Europeana Portal request (with 200 response)
        testNormalResponse(mockMvc.perform(options("/myApi/{someRequest}", "123test")
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header(HttpHeaders.ACCEPT, "*/*")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-site")
                .header("Sec-Fetch-Dest", "empty")
                .header(HttpHeaders.REFERER, "https://www.europeana.eu/en/set/5")
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en;q=0.9,nl;q=0.8")
                .header("dnt", "1")
                .header(HttpHeaders.ORIGIN, "https://test.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, HttpHeaders.AUTHORIZATION)
        ));
    }

    private void testNormalResponse(ResultActions actions) throws Exception {
        actions.andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
    }

    private void testErrorResponse(ResultActions actions) throws Exception {
        actions.andExpect(status().isBadRequest())
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
    }

}
