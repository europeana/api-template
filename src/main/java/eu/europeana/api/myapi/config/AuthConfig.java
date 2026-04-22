package eu.europeana.api.myapi.config;


import eu.europeana.api.commons_sb.definitions.oauth.Role;
import eu.europeana.api.commons_sb.auth.AuthenticationBuilder;
import eu.europeana.api.commons_sb.auth.AuthenticationConfig;
import eu.europeana.api.commons_sb.error.exceptions.ApplicationAuthenticationException;
import eu.europeana.api.commons_sb.error.i18n.I18nService;
import eu.europeana.api.commons_sb.error.i18n.I18nServiceImpl;
import eu.europeana.api.commons_sb.oauth2.EuropeanaBeanNames;
import eu.europeana.api.commons_sb.oauth2.service.authorization.BaseAuthorizationService;
import eu.europeana.api.commons_sb.oauth2.service.impl.EuropeanaClientDetailsService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;

/**
 * Configure the application to use the authentication and default error handling functionality offered by API commons
 * SB3. Optionally you can merge this into the MyApiSettings class.
 */
@Configuration
public class AuthConfig extends BaseAuthorizationService {

    private static final Logger LOG = LogManager.getLogger(AuthConfig.class);

    private static final String BEAN_I18N_SERVICE = "i18nService";
    private static final String BEAN_I18N_MESSAGE_SOURCE = "messageSource";

    @Value("${auth.api.name:}")
    private String authApiName;

    @Value("${auth.token.endpoint:}")
    private String authTokenEndpoint;

    @Value("${auth.token.signature:}")
    private String authTokenSignature;

    // For user credentials authorization
    @Value("${auth.token.grant.params:}")
    private String authTokenGrantParams;

    // For user credentials authorization
    @Value("${auth.apikey.endpoint:}")
    private String authApiKeyEndpoint;

    // Optionally enable/disable authorization for particular features in the configuration
    @Value("${myservice.auth.enabled:true}")
    private String myServiceAuthEnabled;

    @PostConstruct
    private void logImportantSettings() {
        if (StringUtils.isEmpty(authApiKeyEndpoint)) {
            LOG.warn("API key validation is not supported!");
        }
        if (StringUtils.isEmpty(authTokenSignature)) {
            LOG.warn("API token validation is not supported!");
        }
        if (Boolean.parseBoolean(myServiceAuthEnabled)) {
            LOG.info("Authentication for my service is enabled");
        } else {
            LOG.warn("Authentication for my service is disabled");
        }
    }

    @Override
    protected String getApiName() {
        if (this.authApiName == null) {
            return null;
        }
        return this.authApiName.trim(); // we trim to avoid issues with accidentally added spaces
    }

    @Override
    protected String getSignatureKey() {
        return this.authTokenSignature;
    }

    @Override
    protected Role getRoleByName(String s) {
        // not sure when this is used
        return null;
    }

    /**
     * @return true if the application is configured to require authorization for accessing a particular service
     */
    public boolean isMyServicedAuthEnabled() {
        // For some reason the junit tests fail when loading this property directly as boolean value using @Value annotation
        // So as a workaround we parse it here
        return Boolean.parseBoolean(myServiceAuthEnabled);
    }

    @Override
    public Authentication authorizeWriteAccess(HttpServletRequest request, String operation) throws ApplicationAuthenticationException {
        if (isMyServicedAuthEnabled()) {
            return super.authorizeWriteAccess(request, operation);
        }
        return null; // give full access if authorization is enabled
    }

    /**
     * This method should return false to only allow client credentials, set it to true to allow user credentials.
     * The latter will require configuring auth.token.grant.params and auth.apikey.endpoint configuration options
     * @param operation not used
     * @return false always
     */
    @Override
    protected boolean isResourceAccessVerificationRequired(String operation) {
        return false;
    }

    /**
     * Configure the default error messages from API commons with internationalization support (see commons-sb-error)
     * @return MessageSource
     */
    @Bean(name = BEAN_I18N_MESSAGE_SOURCE)
    public MessageSource i18nMessagesSource(){
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return source;
    }

    /**
     * Configure the default error messages service from API commons with internationalization support (see commons-sb-error)
     * @return I18nService
     */
    @Bean(name = BEAN_I18N_SERVICE)
    public I18nService getI18nService() {
        return new I18nServiceImpl(i18nMessagesSource());
    }

    /**
     * Configure the API key and token validation client
     * @return EuropeanaClientDetailsService
     */
    @Bean(name = EuropeanaBeanNames.CLIENT_DETAILS_SERVICE)
    public EuropeanaClientDetailsService getClientDetailsService() {
        EuropeanaClientDetailsService clientDetails = new EuropeanaClientDetailsService();
        if (StringUtils.isEmpty(authTokenEndpoint) || StringUtils.isEmpty(authTokenGrantParams)) {
            LOG.warn("Keycloak token endpoint and/or grant parameters are NOT set!");
        } else {
            clientDetails.setApiKeyServiceUrl(authApiKeyEndpoint);
            AuthenticationConfig config = new AuthenticationConfig(authTokenEndpoint, authTokenGrantParams);
            clientDetails.setAuthHandler(AuthenticationBuilder.newAuthentication(config));
        }
        return clientDetails;
    }
}
