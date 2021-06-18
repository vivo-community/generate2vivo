package eu.tib.exception;

public class SparqlExecutionException extends RuntimeException {
    public SparqlExecutionException(String message) {
        super(message);
    }
}