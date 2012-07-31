package com.googlecode.jsonrpc4j.jsonorg;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A JSON-RPC client.
 *
 */
public class JsonRpcClient {
	
	private static final String JSON_RPC_VERSION = "2.0";
	
	private URL serviceUrl;
	private Random random;
	
	private Proxy connectionProxy 		= Proxy.NO_PROXY;
	private int connectionTimeoutMillis	= 60*1000;
	private int readTimeoutMillis		= 60*1000*2;

	/**
	 * Creates the {@link JsonRpcClient} bound to the given {@code serviceUrl}.  
	 * The headers provided in the {@code headers} map are added to every request
	 * made to the {@code serviceUrl}.
	 * @param serviceUrl the URL
	 */
	public JsonRpcClient(URL serviceUrl) {
		this.serviceUrl = serviceUrl;
		this.random = new Random(System.currentTimeMillis());
	}
	
	/**
	 * @param connectionProxy the connectionProxy to set
	 */
	public void setConnectionProxy(Proxy connectionProxy) {
		this.connectionProxy = connectionProxy;
	}

	/**
	 * @param connectionTimeoutMillis the connectionTimeoutMillis to set
	 */
	public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
		this.connectionTimeoutMillis = connectionTimeoutMillis;
	}

	/**
	 * @param readTimeoutMillis the readTimeoutMillis to set
	 */
	public void setReadTimeoutMillis(int readTimeoutMillis) {
		this.readTimeoutMillis = readTimeoutMillis;
	}
	
	/**
	 * Invokes the given method with the given arguments and returns
	 * an object of the given type, or null if void.
	 * @param methodName the name of the method to invoke
	 * @param arguments the arguments to the method
	 * @param returnType the return type
	 * @return the return value
	 * @throws Exception on error
	 */
	public Object invoke(
		String methodName, Object[] arguments, Class<?> returnType)
		throws Exception {
		return invoke(methodName, arguments, returnType, new HashMap<String, String>());
	}

	/**
	 * Invokes the given method with the given arguments and returns
	 * an object of the given type, or null if void.
	 * @param methodName the name of the method to invoke
	 * @param arguments the arguments to the method
	 * @param returnType the return type
	 * @param headers extra headers to add to the request
	 * @return the return value
	 * @throws Exception on error
	 */
	public Object invoke(
		String methodName, Object[] arguments, Type returnType, 
		Map<String, String> headers)
		throws Exception {
		
		// generate an id
		String id = random.nextLong()+"";
		
		// create the request
		JSONObject request = new JSONObject();
		request.put("id", id);
		request.put("jsonrpc", JSON_RPC_VERSION);
		request.put("method", methodName);
		request.put("params", JSONUtil.toJSON(arguments));
		
		// turn into a JSON object
		String jsonRequest = JSONUtil.toJSONString(request, true);
		
		// create URLConnection
		HttpURLConnection con = (HttpURLConnection)serviceUrl.openConnection(connectionProxy);
		con.setConnectTimeout(connectionTimeoutMillis);
		con.setReadTimeout(readTimeoutMillis);
		con.setAllowUserInteraction(false);
		con.setDefaultUseCaches(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setInstanceFollowRedirects(true);
		con.setRequestMethod("POST");
		
		// add headers
		for (String header : headers.keySet()) {
			con.setRequestProperty(header, headers.get(header));
		}
		con.setRequestProperty("Content-Type", "application/json-rpc");
		
		// open the connection
		con.connect();
		OutputStream ops = con.getOutputStream();
		
		// post the json data;
		ops.write(jsonRequest.getBytes());
		ops.flush();
		
		// read the response
		InputStream ips = con.getInputStream();
		String jsonResponse = "";
		byte[] b = new byte[512];
		for (int numBytes = ips.read(b); numBytes!=-1; numBytes = ips.read(b)) {
			jsonResponse += new String(b, 0, numBytes);
		}
		ips.close();
		con.disconnect();
		
		// turn it into a json response object
		JSONObject response = new JSONObject(jsonResponse);
		
		// throw exception on error
		if (!response.isNull("error")) {
			int code = response.getJSONObject("error").getInt("code");
			String message = response.getJSONObject("error").getString("message");
			throw new JSONException("JSON-RPC error "+code+": "+message);
		}
		
		// otherwise return the result
		return (response.isNull("result"))
			? null
			: JSONUtil.fromJSON(response.get("result"), returnType);
		
	}
	
}
