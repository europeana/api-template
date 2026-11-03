package eu.europeana.api.myapi.config;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Container for all settings that we load from the myapi.properties file and optionally override from
 * myapi.user.properties file.
 */
@Configuration
@PropertySource("classpath:myapi.properties")
@PropertySource(value = "classpath:myapi.user.properties", ignoreResourceNotFound = true)
public class MyApiSettings {

    private static final Logger LOG = LogManager.getLogger(MyApiSettings.class);


    @PostConstruct
    private void logImportantSettings() {
        LOG.info("MyAPI settings:");

    }

}
