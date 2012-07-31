package com.googlecode.jsonrpc4j.jsonorg;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.googlecode.jsonrpc4j.jsonorg.converters.DateTypeConverter;
import com.googlecode.jsonrpc4j.jsonorg.converters.EnumTypeConverter;
import com.googlecode.jsonrpc4j.jsonorg.converters.PrimitiveTypeConverter;
import com.googlecode.jsonrpc4j.jsonorg.converters.URLTypeConverter;

/**
 * Utilities related to JSON.
 *
 */
public class JSONUtil {
	
	private static final int MAX_OBJECT_DEPTH_LEVEL = 20;
	private static LinkedList<TypeConverter> typeConverters = new LinkedList<TypeConverter>();
	static {
		registerTypeConverter(new PrimitiveTypeConverter());
		registerTypeConverter(new URLTypeConverter());
		registerTypeConverter(new DateTypeConverter());
		registerTypeConverter(new EnumTypeConverter());
	}
	
	/**
	 * Registers a type converter.
	 * @param typeConverter the type converter
	 */
	public static void registerTypeConverter(TypeConverter typeConverter) {
		typeConverters.addFirst(typeConverter);
	}

	/**
	 * Creates a JSON string from the given value.
	 * @param value the value
	 * @return the string
	 * @throws Exception on error
	 */
	public static String toJSONString(Object value) 
		throws Exception {
		return toJSONString(value, true);
	}

	/**
	 * Creates a JSON string from the given value.
	 * @param value the value
	 * @param indent whether or not to indent the string
	 * @return the string
	 * @throws Exception on error
	 */
	public static String toJSONString(Object value, Boolean indent) 
		throws Exception {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return JSONObject.numberToString((Number)value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof JSONObject) {
			return ((JSONObject)value).toString((indent)?4:0);
		}
		if (value instanceof JSONArray) {
			return ((JSONArray)value).toString((indent)?4:0);
		}
		return JSONObject.quote(value.toString());
	}

	/**
	 * Creates a JSON object from the given string.
	 * @param json the json string
	 * @return the JSON object
	 * @throws Exception on error
	 */
	public static Object fromJSONString(String json) 
		throws Exception {
		return new JSONObject(json);
	}
	
	/**
	 * Creates a json object from the object.
	 * @param object the object
	 * @return the {@link JSONArray}
	 * @throws Exception on error
	 */
	public static Object toJSON(Object object) 
		throws Exception {
		return toJSON(object, 0);
	}
	
	/**
	 * Creates a {@code Object} from the given {@link JSONObject}.
	 * @param <T> the type of object to create
	 * @param object the object
	 * @param type the type
	 * @return the object
	 * @throws Exception on error
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromJSON(Object object, Type type) 
		throws Exception {
		
		Class<?> clazz = clazz(type);
		
		// null
		if (object==null || object==JSONObject.NULL) {
			return null;
			
		// array / collection
		} else if (JSONArray.class.isAssignableFrom(object.getClass())) {
			return (clazz.isArray())
				? (T)fromJSONArrayToArray((JSONArray)object, clazz.getComponentType())
				: (T)fromJSONArrayToCollection((JSONArray)object, type);
			
		// convert it
		} else if (canConvert(clazz)) {
			return (T)convertFromJSON(object, clazz);
			
		// assume it's an object
		} else {
			return (T)fromJSONObject((JSONObject)object, type);
		}
	}
	
	/**
	 * Creates a json object from the object.
	 * @param object the object
	 * @param level the level
	 * @return the converted object
	 * @throws Exception on errorfromJSON
	 */
	@SuppressWarnings("unchecked")
	private static Object toJSON(Object object, int level) 
		throws Exception {
		
		// null
		if (object==null) {
			return null;
			
		// array
		} else if (object.getClass().isArray()) {
			Object[] objects = (Object[])object;
			List<Object> vals = new ArrayList<Object>();
			for (int i=0; i<objects.length; i++) {
				vals.add(objects[i]);
			}
			return toJSONArray(vals, level);
			
		// collection
		} else if (Collection.class.isAssignableFrom(object.getClass())) {
			return toJSONArray(Collection.class.cast(object), level);
			
		// convert it
		} else if (canConvert(object.getClass())) {
			return convertToJSON(object);
			
		// assume it's an object
		} else {
			return toJSONObject(object, level);
		}
	}
	
	/**
	 * Creates a {@link JSONArray} from the given {@code Object}.
	 * @param objects the objects
	 * @param level the current level
	 * @return the object
	 * @throws Exception on error
	 */
	private static JSONArray toJSONArray(Collection<Object> objects, int level) 
		throws Exception {
		
		// make sure we're not too deep
		if (level>MAX_OBJECT_DEPTH_LEVEL) {
			throw new Exception("MAX_OBJECT_DEPTH_LEVEL met");
		}
		
		// bail early
		if (objects==null) {
			return null;
		}
		
		// the object that we're returning
		JSONArray ret = new JSONArray();
		
		// loop through every object
		for (Object object : objects) {
			ret.put(toJSON(object, level+1));
		}
		
		// return it
		return ret;
	}
	
	/**
	 * Creates a {@link JSONObject} from the given {@code Object}.
	 * @param object the object
	 * @param level the current level
	 * @return the object
	 * @throws Exception on error
	 */
	private static JSONObject toJSONObject(Object object, int level) 
		throws Exception {
		
		// don't go deeper than we should
		if (level>MAX_OBJECT_DEPTH_LEVEL) {
			throw new Exception("MAX_OBJECT_DEPTH_LEVEL met");
		}
		
		// easy if it's null
		if (object==null) {
			return null;
		}
		
		// create the object that we're returning
		JSONObject ret = new JSONObject();
		
		// get properties
		BeanProperty[] properties = getBeanPropertyNames(object.getClass());
		for (BeanProperty property : properties) {
			ret.put(
				property.getPropertyName(), 
				toJSON(property.getValue(object), level+1)
			);
		}
		
		// return the object
		return ret;
	}
	
	/**
	 * Creates a {@code Object} from the given {@link JSONObject}.
	 * @param <T> the type of object to create
	 * @param object the object
	 * @param type the type
	 * @return the object
	 * @throws Exception on error
	 */
	@SuppressWarnings("unchecked")
	private static <T> T fromJSONObject(JSONObject object, Type type) 
		throws Exception {
		
		// easy if it's null
		if (object==null || object==JSONObject.NULL) {
			return null;
		}
		
		// create the object that we're returning
		T ret = (T)clazz(type).newInstance();
		
		// get properties
		BeanProperty[] properties = getBeanPropertyNames(clazz(type));
		for (BeanProperty property : properties) {
			Object jsonValue = object.get(property.getPropertyName());
			property.setValue(ret, fromJSON(jsonValue, property.getType()));
		}
		
		// return the object
		return ret;
	}
	/**
	 * Creates a {@code Collection} from the given {@link JSONArray}.
	 * @param <T> the type
	 * @param array the array
	 * @param collectionType the type of the collection
	 * @return the collection
	 * @throws Exception on error
	 */
	@SuppressWarnings("unchecked")
	private static <T> T fromJSONArrayToCollection(JSONArray array, Type collectionType) 
		throws Exception {
		return (T)fromJSONArrayToCollection(array, collectionType, null);
	}
	
	/**
	 * Creates a {@code Collection} from the given {@link JSONArray}.
	 * @param <T> the type
	 * @param <V> the valueType
	 * @param array the array
	 * @param collectionType the type of the collection
	 * @param valueType the type of the elements in the collection
	 * @return the collection
	 * @throws Exception on error
	 */
	@SuppressWarnings("unchecked")
	private static <T, V> T fromJSONArrayToCollection(
		JSONArray array, Type collectionType, Type valueType) 
		throws Exception {
		
		// bail early
		if (array==null || array==JSONObject.NULL) {
			return null;
		}
		
		// get the type parameter
		Collection ret = null;
		if (List.class.isAssignableFrom(clazz(collectionType))) {
			ret = new ArrayList();
			
		} else if (Set.class.isAssignableFrom(clazz(collectionType))) {
			ret = new HashSet();
			
		} else if (Collection.class.isAssignableFrom(clazz(collectionType))) {
			ret = new ArrayList();
			
		}
		
		if (valueType==null && collectionType instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType)collectionType;
			valueType = clazz(pType.getActualTypeArguments()[0]);
		}
		
		// loop through every object
		for (int i=0; i<array.length(); i++) {
			Object element = array.get(i);
			
			if (valueType==null && canConvert(element.getClass())) {
				ret.add(convertFromJSON(element, element.getClass()));
				
			} else if (valueType!=null) {
				ret.add(fromJSON(element, valueType));
				
			} else {
				throw new IllegalArgumentException(
					"Unable to convert to Collection from JSON because "
					+"valueType couldn't be determined and there wasn't "
					+"a registered TypeConverter");
			}
		}
		
		// return it
		return (T)ret;
	}
	
	/**
	 * Creates an array from the given {@link JSONArray}.
	 * @param <T> the type
	 * @param array the array
	 * @param valueType the type of objects in the array
	 * @return the collection
	 * @throws Exception on error
	 */
	@SuppressWarnings("unchecked")
	private static <T> T[] fromJSONArrayToArray(JSONArray array, Class<T> valueType) 
		throws Exception {
		
		// bail early
		if (array==null || array==JSONObject.NULL) {
			return null;
		}
		
		// loop through every object
		T[] ret = (T[])Array.newInstance(valueType, array.length());
		for (int i=0; i<array.length(); i++) {
			ret[i] = (T)fromJSON(array.get(i), valueType);
		}
		
		// return it
		return ret;
	}
	
	/**
	 * Checks to see if an object can be converted to/from JSON.
	 * @param clazz the class
	 * @return true if it can be converted
	 */
	private static boolean canConvert(Class<?> clazz) {
		for (TypeConverter converter : typeConverters) {
			if (converter.supports(clazz)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Converts to JSON objects.
	 * @param object the object
	 * @return the json object
	 * @throws Exception on error
	 */
	private static Object convertToJSON(Object object) 
		throws Exception {
		if (object==null || object==JSONObject.NULL) {
			return null;
		}
		for (TypeConverter converter : typeConverters) {
			if (converter.supports(object.getClass())) {
				return converter.toJSON(object, object.getClass());
			}
		}
		throw new IllegalArgumentException("Unknown object type: "+object.getClass());
	}
	
	/**
	 * Converts from JSON objects.
	 * @param object the json object
	 * @param clazz the class to convert to
	 * @return the object
	 * @throws Exception on error
	 */
	private static Object convertFromJSON(Object object, Class<?> clazz) 
		throws Exception {
		if (object==null) {
			return JSONObject.NULL;
		}
		for (TypeConverter converter : typeConverters) {
			if (converter.supports(clazz)) {
				return converter.fromJSON(object, clazz);
			}
		}
		throw new IllegalArgumentException("Unknown object type: "+clazz);
	}
	
	/**
	 * Returns all of the methods that are bean properties.
	 * @param clazz the class
	 * @return the bean property method pairs
	 * @throws Exception on error
	 */
	private static BeanProperty[] getBeanPropertyNames(Class<?> clazz) 
		throws Exception {
		Method[] methods = clazz.getMethods();
		
		List<BeanProperty> ret = new ArrayList<BeanProperty>();
		
		// loop through the methods
		for (Method setter : methods) {
			
			// is it a setter?
			if (setter.getName().startsWith("set") 
				&& setter.getParameterTypes().length==1) {
				String propertyName = setter.getName().substring(3);
				
				// find the getter
				for (Method getter : methods) {
					if ((getter.getName().startsWith("get") 
						|| getter.getName().startsWith("is"))
						&& getter.getName().endsWith(propertyName)
						&& getter.getParameterTypes().length==0) {
						
						propertyName 
							= propertyName.substring(0, 1).toLowerCase()
							+ propertyName.substring(1);
						ret.add(new BeanProperty(propertyName, getter, setter));
					}
				}
			}
		}
		return ret.toArray(new BeanProperty[0]);
	}
	
	/**
	 * Returns the {@link Class} for the type.
	 * @param t the class
	 * @return the class
	 */
	@SuppressWarnings("unchecked")
	private static Class<?> clazz(Type t) {
		if (t instanceof Class) {
			return (Class<?>)t;
		} else if (t instanceof ParameterizedType) {
			return clazz(((ParameterizedType)t).getRawType());
		}
		throw new IllegalArgumentException("Type "+t+" is not a class");
	}
	
	/**
	 * Class for holding a pair of methods.
	 */
	public static class BeanProperty {
		
		private String propertyName;
		private Method getter;
		private Method setter;
		private Type type;
		
		/**
		 * Creates it.
		 * @param propertyName the name
		 * @param getter the getter method
		 * @param setter the setter method
		 */
		public BeanProperty(String propertyName, Method getter, Method setter) {
			this.propertyName 	= propertyName;
			this.getter 		= getter;
			this.setter 		= setter;
			this.type			= getter.getGenericReturnType();
		}

		/**
		 * @return the propertyName
		 */
		public String getPropertyName() {
			return propertyName;
		}

		/**
		 * @return the getter
		 */
		public Method getGetter() {
			return getter;
		}

		/**
		 * @return the setter
		 */
		public Method getSetter() {
			return setter;
		}
		
		/**
		 * Returns the value.
		 * @param obj the object
		 * @return the value
		 * @throws Exception on error
		 */
		public Object getValue(Object obj) 
			throws Exception {
			return getter.invoke(obj);
		}
		
		/**
		 * Sets the value.
		 * @param obj the object
		 * @param value the value to set
		 * @throws Exception on error
		 */
		public void setValue(Object obj, Object value) 
			throws Exception {
			setter.invoke(obj, value);
		}

		/**
		 * @return the type
		 */
		public Type getType() {
			return type;
		}
	}
}
