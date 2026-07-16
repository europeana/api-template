package eu.europeana.api.myapi.web;

import eu.europeana.api.commons_sb.auth.service.AuthGrant;
import eu.europeana.api.commons_sb.auth.service.AuthenticationService;
import eu.europeana.api.commons_sb.auth.service.TokenResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for testing the API key and token functionality of the MyApiController class
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:myapi.IT.properties")
class MyApiControllerIT {

    private static final Logger LOG = LogManager.getLogger(MyApiControllerIT.class);

    private static final String DUMMY_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";

    @Autowired
    private MockMvc mockMvc;

    @Value("${test.apikey.valid}")
    private String validApikey;

    @Value("${test.token.expired}")
    private String expiredToken;

    @Value("${auth.token.endpoint}")
    private String tokenEndpoint;

    @Value("${auth.token.grant.params}")
    private String tokenGrantParams;

    @Test
    void testNoApiKey() throws Exception {
        // Note that if no API key service is configured this test will pass
        mockMvc.perform(get("/myApi/apikey"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testApiKeyInvalid() throws Exception {
        // Note that if no API key service is configured this call will return a 200 response
        mockMvc.perform(get("/myApi/apikey").param("wskey", "invalid"))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void testApiKeyValid()  throws Exception {
        // Note that if no API key service is configured this call will return a 200 response
        LOG.info("Checking validity of token {}", expiredToken);
        mockMvc.perform(get("/myApi/apikey").param("wskey", validApikey))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    void testTokenInvalid() throws Exception {
        mockMvc.perform(get("/myApi/token").header(HttpHeaders.AUTHORIZATION, "Bearer " + DUMMY_TOKEN))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void testTokenExpired() throws Exception {
        mockMvc.perform(get("/myApi/token").header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    void testTokenValid() throws Exception {
        AuthenticationService service = new AuthenticationService(this.tokenEndpoint);
        TokenResponse token = service.newToken(new AuthGrant(tokenGrantParams));
        LOG.info("Received token {}", token.accessToken());

        mockMvc.perform(get("/myApi/token").header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken()))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

}
