package eu.europeana.api.myapi.web;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private static final String DUMMY_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testApiKeyInvalid() throws Exception {
        // Note that if no API key service is configured this call will return a 200 response
        mockMvc.perform(get("/myApi/apikey").param("wskey", "invalid"))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void testTokenInvalid() throws Exception {
        mockMvc.perform(get("/myApi/token").header(HttpHeaders.AUTHORIZATION, "Bearer " + DUMMY_TOKEN))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    // TODO add tests for ApiKeyValid + tokenValid

}
