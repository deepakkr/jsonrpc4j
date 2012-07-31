package com.googlecode.jsonrpc4j.jackson;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.deser.StdDeserializerProvider;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.googlecode.jsonrpc4j.JsonEngine;
import com.googlecode.jsonrpc4j.JsonException;
import com.googlecode.jsonrpc4j.JsonRpcError;

public class JacksonJsonEngine 
	implements JsonEngine {

	private JsonFactory jsonFactory;
	private ObjectMapper objectMapper;
	private AliasDeserializerFactory aliasDeserializationFactory;
	private AliasSerializerFactory aliasSerializationFactory;
	
	private Map<org.codehaus.jackson.map.SerializationConfig.Feature, Boolean> 
		serializationFeatures = new HashMap<org.codehaus.jackson.map.SerializationConfig.Feature, Boolean>();
	private Map<Feature, Boolean> deserializationFeatures = new HashMap<Feature, Boolean>();
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 
	 * @param serializer
	 * @param deserializer
	 * @param serializerConfig
	 * @param deserializerConfig
	 */
	public JacksonJsonEngine() {
		
		// create the factory
		jsonFactory = new JsonFactory();
		aliasDeserializationFactory = new AliasDeserializerFactory();
		aliasSerializationFactory = new AliasSerializerFactory();
		
		// create the object mapper
		objectMapper = new ObjectMapper(jsonFactory);
		objectMapper.setDeserializerProvider(new StdDeserializerProvider(aliasDeserializationFactory));
		objectMapper.setSerializerFactory(aliasSerializationFactory);
		objectMapper.getSerializationConfig().setDateFormat(dateFormat);
		objectMapper.getDeserializationConfig().setDateFormat(dateFormat);
		
		// set features
		for (org.codehaus.jackson.map.SerializationConfig.Feature f : serializationFeatures.keySet()) {
			objectMapper.getSerializationConfig().set(f, serializationFeatures.get(f));
		}
		for (Feature f : deserializationFeatures.keySet()) {
			objectMapper.getDeserializationConfig().set(f, deserializationFeatures.get(f));
		}
		
		// set the codec
		jsonFactory.setCodec(objectMapper);
	}

	/**
     * TODO: find a better way of going from JsonNode to Object.
	 * {@inheritDoc}
	 */
	public <T> T jsonToObject(Object json, Class<T> valueType) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof JsonNode)) {
            throw new JsonException(
                "Source is not a JsonNode");
        }
        
        try {
        	// convert to json string
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        JsonGenerator generator = jsonFactory.createJsonGenerator(
	            out, JsonEncoding.UTF8);
	        generator.writeTree(JsonNode.class.cast(json));
	        generator.flush();
	        
	        // create parser
	        return objectMapper.readValue(
	        	new ByteArrayInputStream(out.toByteArray()),
	        	valueType);
	        
        } catch(Exception e) {
        	throw new JsonException(e);
        }
        
	}

	@SuppressWarnings("unchecked")
	public <T> T jsonToObject(Object json, Type valueType) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof JsonNode)) {
            throw new JsonException(
                "Source is not a JsonNode");
        }
        
        try {
        	// convert to json string
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        JsonGenerator generator = jsonFactory.createJsonGenerator(
	            out, JsonEncoding.UTF8);
	        generator.writeTree(JsonNode.class.cast(json));
	        generator.flush();
	        
	        // create parser
	        return (T)objectMapper.readValue(
	        	new ByteArrayInputStream(out.toByteArray()),
	        	TypeFactory.fromType(valueType));
	        
        } catch(Exception e) {
        	throw new JsonException(e);
        }
	}

	/**
     * TODO: find a better way of going from Object to JsonNode.
	 * {@inheritDoc}
	 */
	public <T> Object objectToJson(T obj) 
		throws JsonException {
		
        try {
        	// convert to json string
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        JsonGenerator generator = jsonFactory.createJsonGenerator(
	            out, JsonEncoding.UTF8);
	        generator.writeObject(obj);
	        generator.flush();
	        
	        // convert to JsonNode
	        JsonParser parser = jsonFactory.createJsonParser(
	            new ByteArrayInputStream(out.toByteArray()));
	        return parser.readValueAsTree();
	        
        } catch(Exception e) {
        	throw new JsonException(e);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	public Object validateRpcRequest(Object json) 
		throws JsonException {
		
		// make sure it's what we expect
		if (!(json instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not an ObjectNode");
        } 
        
        // cast
        ObjectNode obj = ObjectNode.class.cast(json);
        JsonNode versionNode = obj.get("jsonrpc");
        JsonNode methodNode = obj.get("method");
        
        // verify version node
        if (versionNode==null 
            || !versionNode.isTextual() 
            || !versionNode.getTextValue().equals("2.0")) {
            throw new JsonException(
                "\"jsonrpc\" attribute not \"2.0\" or not found");
            
        // verify method node
        } else if (methodNode==null
            || !methodNode.isTextual()
            || methodNode.getTextValue().trim().equals("")) {
            throw new JsonException(
                "\"method\" attribute empty or not found");
            
        }
        
        return obj;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object validateRpcBatchRequest(Object json) 
		throws JsonException {
		if (!(json instanceof ArrayNode)) {
            throw new JsonException(
                "Source is not an ArrayNode");
        }
		ArrayNode array = ArrayNode.class.cast(json);
		for (int i=0; i<array.size(); i++) {
			validateRpcRequest(array.get(i));
		}
        return array;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRpcBatchRequest(Object json) 
		throws JsonException {
		return (json instanceof ArrayNode);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNotification(Object json) 
		throws JsonException {
		return (!(json instanceof ObjectNode))
			? false : (
				((ObjectNode)json).get("id")==null
				|| ((ObjectNode)json).get("id").isNull() 
				|| ((ObjectNode)json).get("id").isMissingNode()
			);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Iterator<Object> getRpcBatchIterator(Object json) 
		throws JsonException {
		if (!(json instanceof ArrayNode)) {
            throw new JsonException(
                "Source is not an ArrayNode");
        }
        return (Iterator)ArrayNode.class.cast(json).iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object readJson(InputStream in) 
		throws JsonException {
		try {
			JsonParser parser = jsonFactory.createJsonParser(in);
			return parser.readValueAsTree();
		} catch(Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeJson(Object json, OutputStream out) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof JsonNode)) {
            throw new JsonException(
                "Source is not a JsonNode");
        }
        
        try {
        	// convert to json string
	        JsonGenerator generator = jsonFactory.createJsonGenerator(
	        	out, JsonEncoding.UTF8);
	        generator.writeTree(JsonNode.class.cast(json));
	        generator.flush();
	        
        } catch(Exception e) {
        	throw new JsonException(e);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	public String getIdFromRpcRequest(Object json) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not a ObjectNode");
        }
        
        ObjectNode node = (ObjectNode)json;
        return node.get("id").getValueAsText();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Object> getJsonParametersFromRpcRequest(Object json) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not a ObjectNode");
        }
        
        List<Object> ret = new ArrayList<Object>();
        ObjectNode node = (ObjectNode)json;
        if (node.get("params").isArray()) {
        	ArrayNode array = (ArrayNode)node.get("params");
        	for (int i=0; i<array.size(); i++) {
        		ret.add(array.get(i));
        	}
        	return ret;
        } else {
        	ret.add(node.get("params"));
        }
        return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMethodNameFromRpcRequest(Object json) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not a ObjectNode");
        }
        
        ObjectNode node = (ObjectNode)json;
        return node.get("method").getValueAsText();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRpcRequestParametersIndexed(Object json)
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not a ObjectNode");
        }
        
        ObjectNode node = (ObjectNode)json;
        return node.get("params").isArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getParameterFromRpcRequest(Object json, int index)
		throws JsonException {
		
		// make sure it's what we expect
        if (!isRpcRequestParametersIndexed(json)) {
            throw new JsonException(
                "JSON-RPC request params are not indexed");
        }
        
        return ((ArrayNode)((ObjectNode)json).get("params")).get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getParameterFromRpcRequest(Object json, String name)
		throws JsonException {
		
		// make sure it's what we expect
        if (isRpcRequestParametersIndexed(json)) {
            throw new JsonException(
            	"JSON-RPC request params are not named");
        }
        
        return ((ObjectNode)((ObjectNode)json).get("params")).get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getParameterCountFromRpcRequest(Object json) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(json instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not a ObjectNode");
        }
        
        ObjectNode node = (ObjectNode)json;
        return node.get("params").size();
	}

	/**
	 * {@inheritDoc}
	 */
	public JsonRpcError getJsonErrorFromResponse(Object jsonResponse) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(jsonResponse instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not a ObjectNode");
        }
        
		return jsonToObject(
			((ObjectNode)jsonResponse).get("error"), JsonRpcError.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getJsonResultFromResponse(Object jsonResponse) 
		throws JsonException {
		
		// make sure it's what we expect
        if (!(jsonResponse instanceof ObjectNode)) {
            throw new JsonException(
                "Source is not a ObjectNode");
        }
        
		return ((ObjectNode)jsonResponse).get("result");
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTypeAlias(Class<?> fromType, Class<?> toType) {
		aliasDeserializationFactory.addAlias(fromType, toType);
		aliasSerializationFactory.addAlias(fromType, toType);
	}
	
	/**
	 * Writes common fields to the generator.
	 * @param gen the generator
	 * @param methodName the methodName
	 * @throws IOException on error
	 */
	private void writeCommonRpcRequestFields(ObjectNode rpcRequest, String methodName) {
		rpcRequest.put("jsonrpc", "2.0");
		rpcRequest.put("method", methodName);
		rpcRequest.put("id", UUID.randomUUID().toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public Object createRpcRequest(String methodName, Object[] arguments) 
		throws JsonException {
		
		// create object and write common fields
		ObjectNode rpcRequest = new ObjectNode(JsonNodeFactory.instance);
		writeCommonRpcRequestFields(rpcRequest, methodName);
		
		// add indexed parameters
		ArrayNode params = rpcRequest.putArray("params");
		for (int i=0; i<arguments.length; i++) {
			params.add((JsonNode)objectToJson(arguments[i]));
		}
		
		// return it
		return rpcRequest;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object createRpcRequest(
		String methodName, Map<String, Object> arguments) 
		throws JsonException {
		
		// create object and write common fields
		ObjectNode rpcRequest = new ObjectNode(JsonNodeFactory.instance);
		writeCommonRpcRequestFields(rpcRequest, methodName);
		
		// add named parameters
		ObjectNode params = rpcRequest.putObject("params");
		for (String name : arguments.keySet()) {
			params.put(name, (JsonNode)objectToJson(arguments.get(name)));
		}
		
		// return it
		return rpcRequest;
	}
	/**
	 * For adding a custom JsonDeserializer.
	 * @param <T>
	 * @param forClass
	 * @param deserializer
	 */
	public <T> void addJsonDeserializer(Class<T> forClass, JsonDeserializer<T> deserializer) {
		aliasDeserializationFactory.addSpecificMapping(forClass, deserializer);
	}
	
	/**
	 * For adding a custom JsonSerializer.
	 * @param <T>
	 * @param forClass
	 * @param serializer
	 */
	public <T> void addJsonSerializer(Class<T> forClass, JsonSerializer<T> serializer) {
		aliasSerializationFactory.addSpecificMapping(forClass, serializer);
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @param serializationFeatures the serializationFeatures to set
	 */
	public void setSerializationFeatures(
		Map<org.codehaus.jackson.map.SerializationConfig.Feature, Boolean> serializationFeatures) {
		this.serializationFeatures = serializationFeatures;
	}

	/**
	 * @param deserializationFeatures the deserializationFeatures to set
	 */
	public void setDeserializationFeatures(
		Map<Feature, Boolean> deserializationFeatures) {
		this.deserializationFeatures = deserializationFeatures;
	}
	
}
