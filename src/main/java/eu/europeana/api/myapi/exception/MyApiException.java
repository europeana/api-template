package eu.europeana.api.myapi.exception;

import eu.europeana.api.commons_sb3.error.EuropeanaApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception to demonstrate custom error handling in your application's GlobalExceptionHandler
 * It's recommended that all exceptions created in the API extend the EuropeanaApiException
 */
public class MyApiException extends EuropeanaApiException {

    /**
     * Initialise a new exception for which there is no root cause
     * @param msg error message
     */
    public MyApiException(String msg) {
        super(msg);
    }

    /**
     * Initialise a new exception for which there is no root cause
     * @param msg full error message
     * @param error short error message
     * @param errorCode error code
     */
    public MyApiException(String msg, String error, String errorCode) {
        super(msg, error, errorCode);
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
