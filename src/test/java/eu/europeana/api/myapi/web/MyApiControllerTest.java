package eu.europeana.api.myapi.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit test for testing the RecommendController class
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MyApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMyControllerValidInput() throws Exception {
        // with accept header
        mockMvc.perform(get("/myApi/{someRequest}", "123test")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.OK.value()));

        // without accept header
        mockMvc.perform(get("/myApi/{someRequest}", "123test"))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testMyControllerInvalidInput() throws Exception {
        // without accept header
        mockMvc.perform(get("/myApi/{someRequest}", "123-test"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

    }

    /**
     * Test if CORS works for normal requests and error requests
     */
    @Test
    public void testCORS() throws Exception {
        // normal (200 response) request
        mockMvc.perform(get("/myApi/{someRequest}", "123test")
                .header(HttpHeaders.ORIGIN, "https://test.com"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));

        // error request
        mockMvc.perform(get("/myApi/{someRequest}", "123-test")
                .header(HttpHeaders.ORIGIN, "https://test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
    }

}
