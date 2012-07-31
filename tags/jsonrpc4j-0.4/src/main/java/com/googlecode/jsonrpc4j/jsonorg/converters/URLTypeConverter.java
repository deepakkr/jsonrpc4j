package com.googlecode.jsonrpc4j.jsonorg.converters;

import java.net.URL;

import com.googlecode.jsonrpc4j.jsonorg.TypeConverter;

/**
 * {@link TypeConverter} for {@link URL} objects.
 *
 */
public class URLTypeConverter 
	implements TypeConverter {

	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Class<?> clazz) {
		return clazz.equals(URL.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object fromJSON(Object object, Class<?> clazz) 
		throws Exception {
		return new URL(object.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toJSON(Object object, Class<?> clazz)
		throws Exception {
		return object.toString();
	}

}
