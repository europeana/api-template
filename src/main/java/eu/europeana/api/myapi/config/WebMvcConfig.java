package eu.europeana.api.myapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Setup CORS for all requests and setup default Content-type
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final long CORS_MAX_AGE = 1000; // in seconds

    /**
     * Setup CORS for all GET, HEAD and OPTIONS, requests.
     */
    @Override
    @SuppressWarnings("java:S5122") // most APIs use allowed-origins *
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.OPTIONS.name())
                .exposedHeaders(HttpHeaders.ALLOW,
                        HttpHeaders.CACHE_CONTROL,
                        HttpHeaders.ETAG,
                        HttpHeaders.LAST_MODIFIED)
                .allowedHeaders("*")
                .maxAge(CORS_MAX_AGE); // in seconds
    }

    /*
     * Set default content type in case we receive a request without Accept header
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // set json as default answer, even if no accept header or extension was provided
        configurer.defaultContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE), MediaType.APPLICATION_JSON);
    }
}
