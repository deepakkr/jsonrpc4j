package com.googlecode.jsonrpc4j;

public enum StandardJsonError {
	// based on http://www.jsonrpc.org/historical/json-rpc-over-http.html#errors
	PARSE_ERROR(500,-32700,"Parse error."),
	INVALID_REQUEST(400, -32600, "Invalid Request."),
	METHOD_NOT_FOUND(404, -32601, "Method not found."),
	INVALID_PARAMS(500, -32602, "Invalid params."),
	INTERNAL_ERROR(500, -32603, "Internal error."),
	SERVER_ERROR(500, -32099, "Server error.");
	
	private int httpCode;
	private int jsonCode;
	private String message;
	private StandardJsonError(int httpCode, int jsonCode, String message) {
		this.httpCode = httpCode;
		this.jsonCode = jsonCode;
		this.message = message;
	}
	
	public int getHttpCode() {
		return httpCode;
	}
	
	public int getJsonCode() {
		return jsonCode;
	}
	
	public String getMessage() {
		return message;
	}
}