package eu.europeana.api.myapi;

import eu.europeana.api.myapi.config.SocksProxyConfig;
import eu.europeana.api.myapi.util.SocksProxyActivator;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main application. Allows deploying as a war and logs instance data when deployed in Cloud Foundry
 */
@SpringBootApplication(scanBasePackages = "eu.europeana.api")
public class MyApiApplication extends SpringBootServletInitializer {

    /**
     * Main entry point of this application
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // When deploying to Cloud Foundry, this will log the instance index number, IP and GUID
        LogManager.getLogger(MyApiApplication.class).
                info("CF_INSTANCE_INDEX  = {}, CF_INSTANCE_GUID = {}, CF_INSTANCE_IP  = {}",
                    System.getenv("CF_INSTANCE_INDEX"),
                    System.getenv("CF_INSTANCE_GUID"),
                    System.getenv("CF_INSTANCE_IP"));

        // Activate socks proxy (if your application requires it)
        SocksProxyActivator.activate(new SocksProxyConfig("myapi.properties", "myapi.user.properties"));

        SpringApplication.run(MyApiApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MyApiApplication.class);
    }

}
