package com.googlecode.jsonrpc4j.jsonorg.converters;

import java.util.Date;

import java.text.SimpleDateFormat;

import com.googlecode.jsonrpc4j.jsonorg.TypeConverter;

/**
 * {@link TypeConverter} for {@link Date}s.
 * @author bdilley
 *
 */
public class DateTypeConverter 
	implements TypeConverter {

	private SimpleDateFormat format = null;
	
	/**
	 * Creates the {@link DateTypeConverter} with the
	 * default date format of "yyyy-MM-dd HH:mm:ss,SSS".
	 */
	public DateTypeConverter() {
		this("yyyy-MM-dd HH:mm:ss,SSS");
	}
	
	/**
	 * Creates the {@link DateTypeConverter} with the
	 * given date format.
	 * @param format the format
	 */
	public DateTypeConverter(String format) {
		this.format = new SimpleDateFormat(format);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Class<?> clazz) {
		return Date.class.equals(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object fromJSON(Object object, Class<?> clazz) 
		throws Exception {
		return format.parse(object.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toJSON(Object object, Class<?> clazz) 
		throws Exception {
		return format.format((Date)object);
	}

}
