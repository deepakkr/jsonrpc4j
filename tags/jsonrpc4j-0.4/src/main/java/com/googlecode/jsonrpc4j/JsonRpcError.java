package com.googlecode.jsonrpc4j;

/**
 * <a href="http://groups.google.com/group/json-rpc/web/json-rpc-1-2-proposal">
 * http://groups.google.com/group/json-rpc/web/json-rpc-1-2-proposal</a>
 * 
 * @author brian.dilley@gmail.com
 *
 */
public class JsonRpcError {
    
    private int code;
    private String message;
    private Object data;
    
    /**
     * Creates the object.
     * @param code
     * @param message
     * @param data
     */
    public JsonRpcError(int code, String message, Object data) {
        this.code       = code;
        this.message    = message;
        this.data       = data;
    }
    
    /**
     * Creates the object.
     */
    public JsonRpcError() {
        this(0, null, null);
    }
    
    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }
    
    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }
    
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }
    
    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
    
}
