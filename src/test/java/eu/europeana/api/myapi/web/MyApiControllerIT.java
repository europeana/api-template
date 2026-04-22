package eu.europeana.api.myapi.web;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class MyApiControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testToken() throws Exception {
        ///  TODO
    }


}
