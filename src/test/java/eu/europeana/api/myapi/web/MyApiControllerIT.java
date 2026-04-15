package eu.europeana.api.myapi.web;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for testing the API key and token functionality of the MyApiController class
 */
@Disabled
@SpringBootTest
@AutoConfigureMockMvc
public class MyApiControllerIT {

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
        mockMvc.perform(get("/myApi/{myPath}", "validate-error") // dash character not allowed
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testMyControllerCustomError() throws Exception {
        mockMvc.perform(get("/error3")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.I_AM_A_TEAPOT.value()));
    }

    @Test
    public void testMyControllerNotFoundError() throws Exception {
        mockMvc.perform(get("/not_exists/"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

}
