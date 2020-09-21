package eu.europeana.api.myapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception to demonstrate error handling with ResponseStatus annotation
 */
@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class DummyException extends AbstractApiException {

    /**
     * Initialise a new exception for which there is no root cause
     * @param msg error message
     */
    public DummyException(String msg) {
        super(msg);
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
