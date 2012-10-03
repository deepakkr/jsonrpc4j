package com.googlecode.jsonrpc4j;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcServer.JsonRpcServerResponse;

public interface OutputStreamWrapper {
	public void writeResponse( JsonRpcServerResponse response ) throws IOException;
	
	
	public static class BasicOutputStreamWrapper implements OutputStreamWrapper {
		private ObjectMapper mapper;
		private OutputStream ops;
		
		public BasicOutputStreamWrapper(ObjectMapper mapper, OutputStream ops) {
			this.mapper = mapper;
			this.ops = ops;
		}

		public void writeResponse(JsonRpcServerResponse response) throws IOException {
			mapper.writeValue(new NoCloseOutputStream(ops), response.objectNode);
			ops.flush();
		}
		
	}
	
	public static interface HttpCodeSetter {
		void setHttpCode(int httpCode);
	}
	
	public static class OutputStreamWrapperWithHTTPErrorCode implements OutputStreamWrapper {
		private ObjectMapper mapper;
		private OutputStream ops;
		private HttpCodeSetter httpCodeSetter;
		
		public OutputStreamWrapperWithHTTPErrorCode( ObjectMapper mapper, OutputStream ops, HttpCodeSetter httpCodeSetter) {
			this.mapper = mapper;
			this.ops = ops;
			this.httpCodeSetter = httpCodeSetter;
		}

		public void writeResponse(JsonRpcServerResponse response) throws IOException {
			httpCodeSetter.setHttpCode(response.httpCode);
			mapper.writeValue(new NoCloseOutputStream(ops), response.objectNode);
			ops.flush();
		}
	}
}





