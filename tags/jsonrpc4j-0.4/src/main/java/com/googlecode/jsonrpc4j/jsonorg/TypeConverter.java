package com.googlecode.jsonrpc4j.jsonorg;

/**
 * Interface for the converting of types to and from JSON.
 *
 */
public interface TypeConverter {
	
	/**
	 * Converts the given object to a JSON object.
	 * @param object the object
	 * @param clazz the desired class
	 * @return the converted object
	 * @throws Exception on error
	 */
	Object toJSON(Object object, Class<?> clazz)
		throws Exception;
	
	/**
	 * Converts an object from a JSON object.
	 * @param object the JSON object
	 * @param clazz the desired class
	 * @return the converted object
	 * @throws Exception on error
	 */
	Object fromJSON(Object object, Class<?> clazz)
		throws Exception;
	
	/**
	 * Indicates wheter or not the {@link TypeConverter} can
	 * convert the given {@link Class}.
	 * @param clazz the {@link Class}
	 * @return true if it can convert
	 */
	boolean supports(Class<?> clazz);
	
}
