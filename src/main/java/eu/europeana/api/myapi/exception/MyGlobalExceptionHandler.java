package eu.europeana.api.myapi.exception;

import eu.europeana.api.commons_sb3.error.EuropeanaGlobalExceptionHandler;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

/**
 * Add a class with the @ControllerAdvice annotation that extends the EuropeanaGlobalExceptionHandler
 * Basic error processing (whether to log and error or not, plus handle ConstraintViolations) is done in the
 * EuropeanaGlobalExceptionHandler, but you can add more error handling here. All errors that are not handled will be
 * returned with 500 response
 */
@ControllerAdvice
public class MyGlobalExceptionHandler extends EuropeanaGlobalExceptionHandler {

    /**
     * Add ExceptionHandler methods to handle specific error situations if needed
     * @param e dummy exception to handle
     * @param response response object to sent back
     * @throws IOException when there's a problem sending the error response
     */
    @ExceptionHandler
    @SuppressWarnings("findsecbugs:XSS_SERVLET") // we control error message and use StringEscapeUtils so very low risk
    public void handleDummyExceptions(DummyException e, HttpServletResponse response) throws IOException {
        // do some custom processing here
        // then either rethrow the error or handle it yourself
        response.sendError(HttpStatus.I_AM_A_TEAPOT.value(), StringEscapeUtils.escapeJson(e.getMessage()));
    }

}
