package eu.europeana.api.myapi.exception;

/**
 * Base error class for this application. All other application errors should extend this class
 */
public abstract class AbstractApiException extends Exception {

    /**
     * Initialise a new exception
     * @param msg error message
     * @param t root cause exception
     */
    public AbstractApiException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Initialise a new exception for which there is no root cause
     * @param msg error message
     */
    public AbstractApiException(String msg) {
        super(msg);
    }

    /**
     * By default we log all exceptions, but you can override this method and return false if you do not want an error
     * subclass to log the error
     * @return boolean indicating whether this type of exception should be logged or not.
     */
    public boolean doLog() {
        return true;
    }

    /**
     * By default we log error stacktraces, but you can override this method and return false if you do not want an error
     * subclass to log the error (e.g. in case of common user errors). Note that this only works if doLog is enabled
     * as well.
     * @return boolean indicating whether the stacktrace of the exception should be logged or not.
     */
    public boolean doLogStacktrace() {
        return true;
    }

}
