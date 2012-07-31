package com.googlecode.jsonrpc4j;

import java.io.InputStream;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstraction layer for JSON.  This is kinda dirty :).  I'm not sure if it's
 * worth it - but it does keep the underlying JSON implementation abstracted.
 * @author brian.dilley@gmail.com
 *
 */
public interface JsonEngine {
	
	/**
	 * Ensures that the given object is a
	 * valid JSON-RPC request.
	 * @param json
	 * @return
	 * @throws JsonException
	 */
	Object validateRpcRequest(Object json)
		throws JsonException;
	
	/**
	 * Ensures that the given object is a
	 * valid JSON-RPC batch request.
	 * @param json
	 * @return
	 * @throws JsonException
	 */
	Object validateRpcBatchRequest(Object json)
		throws JsonException;
	
	/**
	 * Checks to see if the given object is
	 * a JSON-RPC batch request.
	 * @param json
	 * @return
	 * @throws JsonException
	 */
	boolean isRpcBatchRequest(Object json)
		throws JsonException;
	
	/**
	 * Checks to see if the given JSON-RPC request
	 * has indexed params vs. keyed (object) params.
	 * @param json
	 * @return
	 * @throws JsonException
	 */
	boolean isRpcRequestParametersIndexed(Object json)
		throws JsonException;
	
	JsonRpcError getJsonErrorFromResponse(Object jsonResponse)
		throws JsonException;
	
	Object getJsonResultFromResponse(Object jsonResponse)
		throws JsonException;
	
	/**
	 * Returns an Iterator for the given JSON-RPC
	 * batch request.
	 * @param json
	 * @return
	 * @throws JsonException
	 */
	Iterator<Object> getRpcBatchIterator(Object json)
		throws JsonException;
	
	/**
	 * Checks to see if the given JSON-RPC request
	 * is a notification.
	 * @param json
	 * @return
	 * @throws JsonException
	 */
	boolean isNotification(Object json)
		throws JsonException;
	
	/**
	 * Converts a JSON to the given type.
	 * @param <T>
	 * @param json
	 * @param valueType
	 * @return
	 * @throws JsonException
	 */
	<T> T jsonToObject(Object json, Class<T> valueType)
		throws JsonException;
	
	/**
	 * Converts a JSON to the given type.
	 * @param <T>
	 * @param json
	 * @param valueType
	 * @return
	 * @throws JsonException
	 */
	<T> T jsonToObject(Object json, Type valueType)
		throws JsonException;
	
	/**
	 * Converts an Object to a JSON.
	 * @param <T>
	 * @param obj
	 * @return
	 * @throws JsonException
	 */
	<T> Object objectToJson(T obj)
		throws JsonException;
	
	/**
	 * Writes the given JSON to the given
	 * OutputStream.
	 * @param json
	 * @param out
	 * @throws JsonException
	 */
	void writeJson(Object json, OutputStream out)
		throws JsonException;
	
	/**
	 * Reads JSON from the given InputStream.
	 * @param in
	 * @return
	 * @throws JsonException
	 */
	Object readJson(InputStream in)
		throws JsonException;
	
	/**
	 * Adds a type alias for conversion.
	 * @param fromType
	 * @param toType
	 * @throws JsonException
	 */
	void addTypeAlias(Class<?> fromType, Class<?> toType)
		throws JsonException;
	
	String getMethodNameFromRpcRequest(Object json)
		throws JsonException;
	
	String getIdFromRpcRequest(Object json)
		throws JsonException;
	
	int getParameterCountFromRpcRequest(Object json)
		throws JsonException;
	
	Object getParameterFromRpcRequest(Object json, int index)
		throws JsonException;
	
	Object getParameterFromRpcRequest(Object json, String name)
		throws JsonException;
	
	Object createRpcRequest(String methodName, Object[] arguments)
		throws JsonException;
	
	Object createRpcRequest(String methodName, Map<String, Object> arguments)
		throws JsonException;
}
