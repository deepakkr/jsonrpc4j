package com.googlecode.jsonrpc4j.jsonorg.converters;

import com.googlecode.jsonrpc4j.jsonorg.TypeConverter;

/**
 * a {@link TypeConverter} for primitives.
 *
 */
public class EnumTypeConverter 
	implements TypeConverter {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object fromJSON(Object object, Class<?> clazz) {
		Class<Enum<?>> enumClazz = (Class<Enum<?>>)clazz;
		for (Enum<?> e : enumClazz.getEnumConstants()) {
			if (e.name().equals(object.toString())) {
				return e;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toJSON(Object object, Class<?> clazz) {
		return ((Enum<?>)object).name();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Class<?> clazz) {
		return Enum.class.isAssignableFrom(clazz);
	}

}
