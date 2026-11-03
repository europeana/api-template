package eu.europeana.api.myapi.exception;

import eu.europeana.api.commons_sb.error.EuropeanaGlobalExceptionHandler;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
// Load exception handling beans from API commons so we always return errors in json format.
// Don't load the EuropeanaGlobalExceptionHandler to prevent loading 2 beans (it's already loaded here because of inheritance)
@ComponentScan(basePackages = "eu.europeana.api.commons_sb.error",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = EuropeanaGlobalExceptionHandler.class))
public class MyGlobalExceptionHandler extends EuropeanaGlobalExceptionHandler {

    private static final Logger LOG = LogManager.getLogger(MyGlobalExceptionHandler.class);

    /**
     * Add ExceptionHandler methods to handle specific error situations if needed
     * @param e dummy exception to handle
     * @param response response object to sent back
     * @throws IOException when there's a problem sending the error response
     */
    @ExceptionHandler
    @SuppressWarnings("findsecbugs:XSS_SERVLET") // we control error message and use StringEscapeUtils so very low risk
    public void handleMyApiExceptions(SomeOtherException e, HttpServletResponse response) throws IOException {
        // Do some custom processing here and then either rethrow the error or handle it yourself
        // Note that by default the error won't be logged if you handle it yourself!
        LOG.error(e);
        response.sendError(HttpStatus.I_AM_A_TEAPOT.value(), StringEscapeUtils.escapeJson(e.getMessage()));
    }


}
