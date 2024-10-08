package eu.europeana.api.myapi.web;

import eu.europeana.api.commons_sb3.error.EuropeanaApiException;
import eu.europeana.api.myapi.exception.DummyException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;

/**
 * Example Rest Controller class with input validation
 */
@RestController
@Validated
public class MyApiController {

    private static final String MY_REGEX = "^[a-zA-Z0-9]*$";
    private static final String INVALID_REQUEST_MESSAGE = "Invalid parameter.";

    /**
     * Test endpoint
     * @param somePath an alpha-numeric String
     * @return just something, doesn't matter what
     */
    @GetMapping(value = "/myApi/{path}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleMyApiRequest(
        @PathVariable(value = "path")
            @Pattern(regexp = MY_REGEX, message = INVALID_REQUEST_MESSAGE) String somePath) {
        return "{ \"You requested\": \"" + somePath + "\" }";
    }

    /**
     * Test endpoint that throws an error and returns a 418 response
     * @param request incoming request
     * @throws EuropeanaApiException thrown always
     */
    @GetMapping(value = "myApi/dummyError")
    public void generateError(HttpServletRequest request) throws EuropeanaApiException {
        if (request.getParameterMap().containsKey("trace") || request.getParameterMap().containsKey("debug")) {
            throw new DummyException("This is an error with stacktrace", "MyErrorCode");
        }
        throw new DummyException("This is an error. You can see a stacktrace if you add a 'trace' or 'debug' parameter",
                "MyErrorCode");
    }
}
