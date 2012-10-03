package com.googlecode.jsonrpc4j.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.OutputStreamWrapper.HttpCodeSetter;
import com.googlecode.jsonrpc4j.OutputStreamWrapper.OutputStreamWrapperWithHTTPErrorCode;
import com.googlecode.jsonrpc4j.StandardJsonError;

public class JsonRpcServerForHttp extends JsonRpcServer {
	
	private static final Logger LOGGER = Logger.getLogger(JsonRpcServerForHttp.class.getName());

	public JsonRpcServerForHttp(Object handler) {
		super(handler);
	}
	
	public JsonRpcServerForHttp(Object handler, Class<?> remoteInterface) {
		super(handler, remoteInterface);
	}


	public JsonRpcServerForHttp(ObjectMapper mapper, Object handler, Class<?> remoteInterface) {
		super(mapper, handler, remoteInterface);
	}

	public JsonRpcServerForHttp(ObjectMapper mapper, Object handler) {
		super(mapper, handler);
	}


	/***
	 * What if I inject the writeValueAndFlush behavior, create an interface for
	 * error/success responses and also an OutputStreamWrapper
	 * 
	 * Maybe also inject the create error response behavior by have an interface or something
	 * for the standard error codes
	 */
	
	public void handleQuietly(final HttpServletRequest request, final HttpServletResponse response) {
		// create http code setter
		HttpCodeSetter httpCodeSetter = new HttpCodeSetter() {
			
			public void setHttpCode(int httpCode) {
				response.setStatus(httpCode);
			}
		};
		OutputStreamWrapperWithHTTPErrorCode opsw = null;
		
		try {
			opsw = new OutputStreamWrapperWithHTTPErrorCode(mapper, response.getOutputStream(), httpCodeSetter);
		} catch (IOException e1) {
			LOGGER.log(Level.WARNING, "Exception getting the response's output stream", e1);
			try {
				response.sendError(500);
			} catch (IOException e2) {
				// if this doesn't work just log and return, don't do anything else
				LOGGER.log(Level.WARNING, "What's going on, I can't even send a 500 error, arrgh", e2);
				return;
			}
		}
		
		try {
			InputStream ips = request.getInputStream();
			if (ips==null) {
				writeAndFlushResponseQuietly(opsw, createErrorResponse("2.0", 0, StandardJsonError.INVALID_REQUEST, null));
				return;
			}
			
			super.handle( request.getInputStream(), opsw );
		} catch (JsonParseException pe) {
			LOGGER.log(Level.WARNING, "JsonParseException when handling request", pe);
			writeAndFlushResponseQuietly(opsw, createErrorResponse("2.0", "null", StandardJsonError.PARSE_ERROR, null));
		} catch ( JsonMappingException me ) {
			// TODO: figure out another way to invoke this exception besides no data
			LOGGER.log(Level.WARNING, "JsonMappingException when handling request", me);
			JsonRpcServerResponse resp = createErrorResponse("2.0", "null", StandardJsonError.INVALID_REQUEST, null);
			writeAndFlushResponseQuietly(opsw, resp);
		} catch ( Exception e ) {
			LOGGER.log(Level.WARNING, "Uncaught exception when handling request", e);
			JsonRpcServerResponse resp = createErrorResponse("2.0","null", StandardJsonError.INTERNAL_ERROR, null);
			writeAndFlushResponseQuietly(opsw, resp);
		}
	}

	private void writeAndFlushResponseQuietly(OutputStreamWrapperWithHTTPErrorCode opsw, JsonRpcServerResponse resp) {
		try {
			writeAndFlushResponse(opsw, resp);
		} catch (Exception e) {
			// if even here there is an exception just set the code to 500 and don't write anything
			// although this may not even work because the http code may have been preset elsewhere
			// aaaaah I just realized I can't even manually set the code, you know, just log the error and return
			LOGGER.log(Level.SEVERE, "There was a problem writing the response to the output stream", e);
		}
		
	}
}
