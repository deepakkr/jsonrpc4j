package com.googlecode.jsonrpc4j;

/**
 * Thrown when JSON data couldn't be parsed.
 * 
 * @author brian.dilley@gmail.com
 *
 */
@SuppressWarnings("serial")
public class JsonException 
    extends Exception {
    
    public JsonException(String message) {
        super(message);
    }
    
    public JsonException(Throwable rootCause) {
        super(rootCause);
    }
    
    public JsonException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
    
}
