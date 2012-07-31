package com.googlecode.jsonrpc4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * For testing the @JsonRpcErrors and @JsonRpcError annotations
 * 
 * @author Hans Jørgen Hoel (hansjorgen.hoel@nhst.no)
 *
 */
public class JsonRpcErrorsTest {

	private static final String JSON_ENCODING = "UTF-8";
	private static final String JSON_FILE = "jsonRpcErrorTest.json";

	private ObjectMapper mapper;
	private ByteArrayOutputStream baos;
	private TestException testException;
	private TestException testExceptionWithMessage;

	@Before
	public void setup() {
		mapper = new ObjectMapper();
		baos = new ByteArrayOutputStream();
		testException = new TestException();
		testExceptionWithMessage = new TestException("exception message");
	}

	@Test
	public void exceptionWithoutAnnotatedServiceInterface() throws Exception {
		JsonRpcServer jsonRpcServer = new JsonRpcServer(mapper, new Service(), ServiceInterfaceWithoutAnnotation.class);
		jsonRpcServer.handle(new ClassPathResource(JSON_FILE).getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);        
		JsonNode json = mapper.readTree(response);
		JsonNode error = json.get("error");

		assertNotNull(error);        
		assertEquals(0, error.get("code").getIntValue());
		assertEquals(testException.getMessage(), error.get("message").getTextValue());
		assertEquals(TestException.class.getName(), error.get("data").getTextValue());        
	}

	@Test
	public void exceptionWithAnnotatedServiceInterface() throws Exception {
		JsonRpcServer jsonRpcServer = new JsonRpcServer(mapper, new Service(), ServiceInterfaceWithAnnotation.class);
		jsonRpcServer.handle(new ClassPathResource(JSON_FILE).getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);        
		JsonNode json = mapper.readTree(response);
		JsonNode error = json.get("error");

		assertNotNull(error);        
		assertEquals(1234, error.get("code").getIntValue());
		assertEquals("", error.get("message").getTextValue());
		assertNull(error.get("data"));        
	}

	@Test
	public void exceptionWithAnnotatedServiceInterfaceMessageAndData() throws Exception {
		JsonRpcServer jsonRpcServer = new JsonRpcServer(mapper, new Service(), ServiceInterfaceWithAnnotationMessageAndData.class);
		jsonRpcServer.handle(new ClassPathResource(JSON_FILE).getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		JsonNode error = json.get("error");

		assertNotNull(error);        
		assertEquals(-5678, error.get("code").getIntValue());
		assertEquals("The message", error.get("message").getTextValue());
		assertEquals("The data", error.get("data").getTextValue());
	}
	
	@Test
	public void exceptionWithMsgInException() throws Exception {
		JsonRpcServer jsonRpcServer = new JsonRpcServer(mapper, new ServiceWithExceptionMsg(), ServiceInterfaceWithAnnotation.class);
		jsonRpcServer.handle(new ClassPathResource(JSON_FILE).getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		JsonNode error = json.get("error");

		assertNotNull(error);        
		assertEquals(1234, error.get("code").getIntValue());
		assertEquals("", error.get("message").getTextValue());
		assertEquals(testExceptionWithMessage.getMessage(), error.get("data").getTextValue());
	}

	private interface ServiceInterfaceWithoutAnnotation {        
		public Object testMethod();        
	}

	private interface ServiceInterfaceWithAnnotation {
		@JsonRpcErrors({@JsonRpcError(exception=TestException.class, code=1234) })
		public Object testMethod();
	}

	private interface ServiceInterfaceWithAnnotationMessageAndData {
		@JsonRpcErrors({@JsonRpcError(exception=TestException.class, code=-5678,
				message="The message", data="The data") })
				public Object testMethod();
	}

	private class Service implements ServiceInterfaceWithoutAnnotation,
	ServiceInterfaceWithAnnotation, ServiceInterfaceWithAnnotationMessageAndData {
		public Object testMethod() {
			throw testException;            
		}
	}
	
	private class ServiceWithExceptionMsg implements ServiceInterfaceWithoutAnnotation,
	ServiceInterfaceWithAnnotation, ServiceInterfaceWithAnnotationMessageAndData {
		public Object testMethod() {
			throw testExceptionWithMessage;            
		}
	}

	private class TestException extends RuntimeException {
				
		private static final long serialVersionUID = 1L;
		
		public TestException() {}
		
		public TestException(String msg) {
			super(msg);
		}
	}
}
