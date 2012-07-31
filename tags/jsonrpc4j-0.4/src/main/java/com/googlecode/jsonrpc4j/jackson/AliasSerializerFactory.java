package com.googlecode.jsonrpc4j.jackson;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

public class AliasSerializerFactory 
	extends CustomSerializerFactory {

	private Map<Class<?>, Class<?>> aliasMap = new HashMap<Class<?>, Class<?>>();
	
	public void addAlias(Class<?> from, Class<?> to) {
		aliasMap.put(from, to);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonSerializer<Object> findBeanSerializer(
		Class<?> type, SerializationConfig config) {
		
		// get the aliased type
		Class<?> aliasedType = aliasMap.get(type);
		
		// check the alias map
		return (aliasedType!=null)
			? super.findBeanSerializer(aliasedType, config)
			: super.findBeanSerializer(type, config);
	}
	
}
