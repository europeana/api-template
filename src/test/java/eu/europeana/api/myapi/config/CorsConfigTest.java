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
     * Test if CORS works for Options normal requests and error requests
     */
    @Test
    public void testCORSOptions() throws Exception {
        // normal (200 response) request
        testNormalResponse(mockMvc.perform(options("/myApi/{someRequest}", "123test")
                .header(HttpHeaders.ORIGIN, "https://test.com")));
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
