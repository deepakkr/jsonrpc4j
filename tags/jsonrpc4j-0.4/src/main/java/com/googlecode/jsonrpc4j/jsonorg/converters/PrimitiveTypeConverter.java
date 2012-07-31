package com.googlecode.jsonrpc4j.jsonorg.converters;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.googlecode.jsonrpc4j.jsonorg.TypeConverter;

/**
 * a {@link TypeConverter} for primitives.
 *
 */
public class PrimitiveTypeConverter 
	implements TypeConverter {

	/**
	 * {@inheritDoc}
	 */
	public Object fromJSON(Object object, Class<?> clazz) {
		if (Byte.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).byteValue();

		} else if (Short.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).shortValue();
			
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).intValue();
			
		} else if (Long.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).longValue();
			
		} else if (Float.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).floatValue();
			
		} else if (Double.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).doubleValue();
			
		} else if (BigDecimal.class.isAssignableFrom(clazz)) {
			return new BigDecimal(object.toString());
			
		} else if (BigInteger.class.isAssignableFrom(clazz)) {
			return new BigInteger(object.toString());
			
		} else {
			return object;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toJSON(Object object, Class<?> clazz) {
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Class<?> clazz) {
		return clazz.isPrimitive()
			|| Boolean.class.isAssignableFrom(clazz)
			|| Number.class.isAssignableFrom(clazz)
			|| String.class.isAssignableFrom(clazz);
	}

}
