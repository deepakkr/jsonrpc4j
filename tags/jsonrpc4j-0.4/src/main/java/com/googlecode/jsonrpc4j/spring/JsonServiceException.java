package com.googlecode.jsonrpc4j.spring;

/**
 * An exception that can be thrown by a service implementation that will be
 * translated propperly into a JSON-RPC error.
 * 
 * @author brian.dilley@gmail.com
 *
 */
@SuppressWarnings("serial")
public class JsonServiceException 
	extends RuntimeException {

    private int code;
    private Object data;
    
    public JsonServiceException(
    	int code, String message, Object data, Throwable cause) {
    	super(message, cause);
    	this.code 			= code;
    	this.data			= data;
    }
    
    public JsonServiceException(
    	int code, String message, Object data) {
    	this(code, message, data, null);
    }
    
    public JsonServiceException(
    	int code, String message, Throwable cause) {
    	this(code, message, null, cause);
    }
    
    public JsonServiceException(
    	int code, String message) {
    	this(code, message, null, null);
    }
    
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	
}
