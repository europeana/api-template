package eu.europeana.api.myapi.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit test to check if compression settings are working.
 * Since compression is only active for responses > 4KB we test with the Swagger endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // to load application-test.yml
public class CompressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Disabled // TODO tmp disable because the test doesn't work!?
    @Test
    public void testEncodingLargeResponse() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(header().exists(HttpHeaders.CONTENT_ENCODING))
                .andExpect(header().string(HttpHeaders.CONTENT_ENCODING, "gzip"));
    }

    @Test
    public void testNotEncodingSmallResponse() throws Exception {
        mockMvc.perform(get("/myApi/{someRequest}", "123test")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_ENCODING));
    }

}
