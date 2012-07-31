package com.googlecode.jsonrpc4j.jackson;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.deser.CustomDeserializerFactory;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

public class AliasDeserializerFactory 
	extends CustomDeserializerFactory {

	private Map<Class<?>, Class<?>> aliasMap = new HashMap<Class<?>, Class<?>>();
	
	public void addAlias(Class<?> from, Class<?> to) {
		aliasMap.put(from, to);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonDeserializer<Object> createBeanDeserializer(
		DeserializationConfig config, JavaType type, DeserializerProvider p) 
		throws JsonMappingException {
		
		// get the aliased type
		Class<?> aliasedType = aliasMap.get(type.getRawClass());
		
		// check the alias map
		return (aliasedType!=null)
			? super.createBeanDeserializer(config, TypeFactory.fromClass(aliasedType), p)
			: super.createBeanDeserializer(config, type, p);
	}
	
}
