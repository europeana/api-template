package eu.europeana.api.myapi.web;

import eu.europeana.api.commons_sb.error.EuropeanaApiException;
import eu.europeana.api.commons_sb.error.exceptions.ApplicationAuthenticationException;
import eu.europeana.api.myapi.config.AuthConfig;
import eu.europeana.api.myapi.exception.SomeException;
import eu.europeana.api.myapi.exception.SomeOtherException;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Example Rest Controller class with input validation
 */
@RestController
@Validated
public class MyApiController {

    private static final String MY_REGEX = "^[a-zA-Z0-9]*$";
    private static final String INVALID_REQUEST_MESSAGE = "Invalid parameter.";

    private final AuthConfig authConfig;

    /**
     * Initialize a new controller
     * @param authConfig autowired configuration
     */
    @Autowired
    public MyApiController(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    /**
     * Simple test endpoint with input validation
     * @param somePath any alphanumerical string
     * @return string
     */
    @GetMapping(value = "/myApi/{path}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleSimpleRequest(
            @PathVariable(value = "path")
            @Pattern(regexp = MY_REGEX, message = INVALID_REQUEST_MESSAGE) String somePath) {
        return "{ \"You requested\": \"" + somePath + "\" }";
    }

    /**
     * Generate a runtime error that is handled by the API commons error functionality. Note that the stacktrace will
     * always be logged and that the 'debug' option to show the 'trace' field is supported
     * @throws RuntimeException thrown always
     */
    @GetMapping(value = "/error1")
    @SuppressWarnings("java:S112") // we deliberately throw a RuntimeException here
    public void generateRuntimeError() {
        throw new RuntimeException("This is a runtime error");
    }

    /**
     * Generate an API exception that is handled by the API commons error functionality
     * @param request the current request that is processed
     * @throws EuropeanaApiException thrown always
     */
    @GetMapping(value = "/error2")
    public void generateBasicError(HttpServletRequest request) throws EuropeanaApiException {
        // TODO currently the trace and/or debug options are not working here. Also we get an unexpected seeAlso field value
        if (request.getParameterMap().containsKey("trace") || request.getParameterMap().containsKey("debug")) {
            throw new SomeException("This is a basic error with stacktrace", "BasicError stacktrace", "BasicErrorCode");
        }
        throw new SomeException("This is a basic error. You can see a stacktrace if you add a 'trace' or 'debug' parameter",
                "BasicError without stacktrace", "BasicErrorCode");
    }

    /**
     * Test endpoint that throws an specific error that is handled by this application (and returns a 418 response)
     * @param request incoming request
     * @throws EuropeanaApiException thrown always
     */
    @GetMapping(value = "/error3")
    public void generateCustomError(HttpServletRequest request) throws EuropeanaApiException {
        // TODO trace option doesn't work anymore
        if (request.getParameterMap().containsKey("trace") || request.getParameterMap().containsKey("debug")) {
            throw new SomeOtherException("This is an error with stacktrace", "MyError stacktrace", "MyErrorCode");
        }
        throw new SomeOtherException("This is an error. You can see a stacktrace if you add a 'trace' or 'debug' parameter",
                "MyError without stacktrace", "MyErrorCode");
    }

    /**
     * Test endpoint with a mandatory wskey parameter
     * Note that SB returns a 400 response when the wskey parameter is not provided. Altenatively without the @RequestParam
     * API commons key validation will return a 401 if no token is provided.
     * @param wskey required wskey parameter
     * @param request the current request that is processed
     * @return response
     * @throws ApplicationAuthenticationException when the provided wskey value is not a valid API key
     */
    @GetMapping(value = "/myApi/apikey", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleApikeyRequest(@RequestParam(value = "wskey") String wskey,
                                                      HttpServletRequest request) throws ApplicationAuthenticationException {
        this.authConfig.authorizeReadAccess(request);
        return new ResponseEntity<>("{ \"API key access\": \"ok\" }", HttpStatus.OK);
    }

    /**
     * Test endpoint with a mandatory authorization token, e.g. for writing access.
     * Note that SB returns a 400 response when the token is not provided. Alternatively without the @RequestHeader
     * API commons key validation will return a 401 if no token is provided.
     * @param authHeader required authorization header
     * @param request the current request that is processed
     * @return response
     * @throws ApplicationAuthenticationException when the provided token is not valid
     */
    @GetMapping(value = "/myApi/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleTokenRequest(@RequestHeader("authorization") String authHeader,
                                     HttpServletRequest request) throws ApplicationAuthenticationException {
        //this.authConfig.authorizeWriteAccess(request, Operations.UPDATE);   // Alternatively you can use authorizeReadAccess
        this.authConfig.authorizeReadAccess(request);
        return "{ \"Token access authorized\" }";
    }


}
