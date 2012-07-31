package com.googlecode.jsonrpc4j;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests for JsonRpcServer
 * 
 * @author Hans Jørgen Hoel (hansjorgen.hoel@nhst.no)
 *
 */
public class JsonRpcServerTest {

	private static final String JSON_ENCODING = "UTF-8";

	private ObjectMapper mapper;
	private ByteArrayOutputStream baos;

	private JsonRpcServer jsonRpcServer;

	private JsonRpcServer jsonRpcServerAnnotatedParam;

	@Before
	public void setup() {
		mapper = new ObjectMapper();
		baos = new ByteArrayOutputStream();
		jsonRpcServer = new JsonRpcServer(mapper, new Service(), ServiceInterface.class);
		jsonRpcServerAnnotatedParam = new JsonRpcServer(mapper, new Service(), ServiceInterfaceWithParamNameAnnotaion.class);
	}

    @Test
    public void receiveJsonRpcNotification() throws Exception {
        jsonRpcServer.handle(new ClassPathResource("jsonRpcServerNotificationTest.json").getInputStream(), baos);
        assertEquals(0, baos.size());
    }

	// Call method that takes one parameter with zero, one and two parameters.
	// Do this with both parameter list (positional) and with named parameters.
	// Should fail when called with zero parameters and when called with two
	// parameters as long as allowExtraParams not set.
	
	@Test
	public void callMethodWithTooFewParameters() throws Exception {		
		jsonRpcServer.handle(new ClassPathResource("jsonRpcServerTooFewParamsTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		// Method not found
		assertEquals(-32601, json.get("error").get("code").getIntValue());		
	}
	
	@Test
	public void callMethodExactNumberOfParameters() throws Exception {
		jsonRpcServer.handle(new ClassPathResource("jsonRpcServerExactParamsTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		assertEquals("success", json.get("result").getTextValue());
	}
	
	@Test
	public void callMethodWithExtraParameter() throws Exception {
		jsonRpcServer.handle(new ClassPathResource("jsonRpcServerExtraParamsTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		// Method not found
		assertEquals(-32601, json.get("error").get("code").getIntValue());
	}
	
	@Test
	public void callMethodWithTooFewParametersNamed() throws Exception {
		jsonRpcServerAnnotatedParam.handle(new ClassPathResource("jsonRpcServerTooFewParamsNamedTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		// Method not found
		assertEquals(-32601, json.get("error").get("code").getIntValue());		
	}
	
	@Test
	public void callMethodExactNumberOfParametersNamed() throws Exception {
		jsonRpcServerAnnotatedParam.handle(new ClassPathResource("jsonRpcServerExactParamsNamedTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		assertEquals("success", json.get("result").getTextValue());
	}
	
	@Test
	public void callMethodWithExtraParameterNamed() throws Exception {
		jsonRpcServerAnnotatedParam.handle(new ClassPathResource("jsonRpcServerExtraParamsNamedTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		// Method not found
		assertEquals(-32601, json.get("error").get("code").getIntValue());
	}
	
	// Repeat tests with allowExtraParams set to true.
	// Should now return "success" even when called with two parameters.
	
	@Test
	public void callMethodWithTooFewParametersAllowOn() throws Exception {
		jsonRpcServer.setAllowExtraParams(true);
		jsonRpcServer.handle(new ClassPathResource("jsonRpcServerTooFewParamsTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		// Method not found
		assertEquals(-32601, json.get("error").get("code").getIntValue());		
	}
	
	@Test
	public void callMethodExactNumberOfParametersAllowOn() throws Exception {
		jsonRpcServer.setAllowExtraParams(true);
		jsonRpcServer.handle(new ClassPathResource("jsonRpcServerExactParamsTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		assertEquals("success", json.get("result").getTextValue());
	}
	
	@Test
	public void callMethodWithExtraParameterAllowOn() throws Exception {
		jsonRpcServer.setAllowExtraParams(true);
		jsonRpcServer.handle(new ClassPathResource("jsonRpcServerExtraParamsTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);

		assertEquals("success", json.get("result").getTextValue());
	}
	
	@Test
	public void callMethodWithTooFewParametersNamedAllowOn() throws Exception {
		jsonRpcServerAnnotatedParam.setAllowExtraParams(true);
		jsonRpcServerAnnotatedParam.handle(new ClassPathResource("jsonRpcServerTooFewParamsNamedTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		// Method not found
		assertEquals(-32601, json.get("error").get("code").getIntValue());		
	}
	
	@Test
	public void callMethodExactNumberOfParametersNamedAllowOn() throws Exception {
		jsonRpcServerAnnotatedParam.setAllowExtraParams(true);
		jsonRpcServerAnnotatedParam.handle(new ClassPathResource("jsonRpcServerExactParamsNamedTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		assertEquals("success", json.get("result").getTextValue());
	}
	
	@Test
	public void callMethodWithExtraParameterNamedAllowOn() throws Exception {
		jsonRpcServerAnnotatedParam.setAllowExtraParams(true);
		jsonRpcServerAnnotatedParam.handle(new ClassPathResource("jsonRpcServerExtraParamsNamedTest.json").getInputStream(), baos);

		String response = baos.toString(JSON_ENCODING);
		JsonNode json = mapper.readTree(response);
		
		assertEquals("success", json.get("result").getTextValue());
	}
	

	// Service and service interfaces used in test
	
	private interface ServiceInterface {        
		public String testMethod(String param1); 
	}
	
	private interface ServiceInterfaceWithParamNameAnnotaion {        
		public String testMethod(@JsonRpcParamName("param1") String param1);        
	}

	private class Service implements ServiceInterface, ServiceInterfaceWithParamNameAnnotaion {
		public String testMethod(String param1) {
			return "success";
		}
	}
	
}
