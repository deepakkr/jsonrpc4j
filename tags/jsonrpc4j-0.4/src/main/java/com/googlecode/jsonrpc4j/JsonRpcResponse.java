package com.googlecode.jsonrpc4j;

/**
 * <a href="http://groups.google.com/group/json-rpc/web/json-rpc-1-2-proposal">
 * http://groups.google.com/group/json-rpc/web/json-rpc-1-2-proposal</a>
 * 
 * @author brian.dilley@gmail.com
 *
 */
public class JsonRpcResponse {
    
	private String jsonrpc		= "2.0";
    private Object result		= null;
    private JsonRpcError error	= null;
    private String id			= null;
    
    public JsonRpcResponse(Object result, JsonRpcError error, String id) {
        this.result = result;
        this.error  = error;
        this.id     = id;
    }
    
    public JsonRpcResponse() {
        this(null, null, null);
    }
    
    public static JsonRpcResponse createError(int code, String message, Object data, String id) {
    	return new JsonRpcResponse(null, new JsonRpcError(code, message, data), id);
    }
    
    public static JsonRpcResponse createResponse(Object result, String id) {
    	return new JsonRpcResponse(result, null, id);
    }
    
    /**
     * @return the result
     */
    public Object getResult() {
        return result;
    }
    
    /**
     * @param result the result to set
     */
    public void setResult(Object result) {
        this.result = result;
    }
    
    /**
     * @return the error
     */
    public JsonRpcError getError() {
        return error;
    }
    
    /**
     * @param error the error to set
     */
    public void setError(JsonRpcError error) {
        this.error = error;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

	/**
	 * @return the jsonrpc
	 */
	public String getJsonrpc() {
		return jsonrpc;
	}

	/**
	 * @param jsonrpc the jsonrpc to set
	 */
	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}
    
}
