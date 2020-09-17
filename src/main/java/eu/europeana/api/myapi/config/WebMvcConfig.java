package eu.europeana.api.myapi.config;

import org.springframework.context.annotation.Configuration;
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

    /**
     * Setup CORS for all requests.
     */
    // Sep 2020: for unknown reason CORS for OPTIONS requests only works properly when CORS Mappings are defined
    // in a WebMvcConfigurer bean. Also we have to explicitly specify OPTIONS in the allowed methods.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.OPTIONS.name())
                .maxAge(1000L); // in seconds
    }

    /*
     * Enable content negotiation via path extension (as long as Spring supports it) and set default content type in
     * case we receive a request without an extension or Accept header
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Enable content negotiation via path extension. Note that this is deprecated with Spring 5.2.4,
        // (see also https://github.com/spring-projects/spring-framework/issues/24179), so it may not work in future
        // releases
        configurer.favorPathExtension(true);

        // set json as default answer, even if no accept header or extension was provided
        configurer.defaultContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE), MediaType.APPLICATION_JSON);
    }
}
