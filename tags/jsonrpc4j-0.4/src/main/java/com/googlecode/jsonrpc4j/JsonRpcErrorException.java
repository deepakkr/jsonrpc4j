package com.googlecode.jsonrpc4j;

/**
 * An {@link Exception} that contanis a {@link JsonRpcError}.
 * 
 * @author brian.dilley@gmail.com
 *
 */
@SuppressWarnings("serial")
public class JsonRpcErrorException 
    extends Exception {
    
	private JsonRpcError error;
	
    public JsonRpcErrorException(JsonRpcError error) {
        super(error.getMessage());
        this.error = error;
    }

	/**
	 * @return the error
	 */
	public JsonRpcError getError() {
		return error;
	}
    
}
