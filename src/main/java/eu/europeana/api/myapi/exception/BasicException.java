package eu.europeana.api.myapi.exception;

import eu.europeana.api.commons_sb3.error.EuropeanaApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception to demonstrate API commons error handling
 * It's recommended that all exceptions created in the API extend the EuropeanaApiException
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE) // TODO no longer works!?
public class BasicException extends EuropeanaApiException {

    private static final HttpStatus responseCode = HttpStatus.NOT_ACCEPTABLE;
    /**
     * Initialise a new exception for which there is no root cause
     * @param msg error message
     */
    public BasicException(String msg) {
        super(msg);
        this.setResponseStatus(responseCode);
    }

    /**
     * Initialise a new exception for which there is no root cause
     * @param msg full error message
     * @param error short error message
     * @param errorCode error code
     */
    public BasicException(String msg, String error, String errorCode) {
        super(msg, error, errorCode);
        this.setResponseStatus(responseCode);
    }

    /**
     * We don't want to log the stack trace for this exception
     * @return false
     */
    @Override
    public boolean doLogStacktrace() {
        return false;
    }


}
