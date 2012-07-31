package com.googlecode.jsonrpc4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utilities for reflection.
 */
public abstract class ReflectionUtil {

	private static Map<String, Set<Method>> methodCache
		= new HashMap<String, Set<Method>>();

	private static Map<Method, List<Class<?>>> parameterTypeCache
		= new HashMap<Method, List<Class<?>>>();

	private static Map<Method, List<Annotation>> methodAnnotationCache
		= new HashMap<Method, List<Annotation>>();

	private static Map<Method, List<List<Annotation>>> methodParamAnnotationCache
		= new HashMap<Method, List<List<Annotation>>>();

	/**
	 * Finds methods with the given name on the given class.
	 * @param clazz the class
	 * @param name the method name
	 * @return the methods
	 */
	public static Set<Method> findMethods(Class<?> clazz, String name) {
		String cacheKey = clazz.getName().concat("::").concat(name);
		if (methodCache.containsKey(cacheKey)) {
			return methodCache.get(cacheKey);
		}
		Set<Method> methods = new HashSet<Method>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(name)) {
				methods.add(method);
			}
		}
		methods = Collections.unmodifiableSet(methods);
		methodCache.put(cacheKey, methods);
		return methods;
	}

	/**
	 * Returns the parameter types for the given {@link Method}.
	 * @param method the {@link Method}
	 * @return the parameter types
	 */
	public static List<Class<?>> getParameterTypes(Method method) {
		if (parameterTypeCache.containsKey(method)) {
			return parameterTypeCache.get(method);
		}
		List<Class<?>> types = new ArrayList<Class<?>>();
		for (Class<?> type : method.getParameterTypes()) {
			types.add(type);
		}
		types = Collections.unmodifiableList(types);
		parameterTypeCache.put(method, types);
		return types;
	}

	/**
	 * Returns all of the {@link Annotation}s defined on
	 * the given {@link Method}.
	 * @param method the {@link Method}
	 * @return the {@link Annotation}s
	 */
	public static List<Annotation> getAnnotations(Method method) {
		if (methodAnnotationCache.containsKey(method)) {
			return methodAnnotationCache.get(method);
		}
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (Annotation a : method.getAnnotations()) {
			annotations.add(a);
		}
		annotations = Collections.unmodifiableList(annotations);
		methodAnnotationCache.put(method, annotations);
		return annotations;
	}

	/**
	 * Returns {@link Annotation}s of the given type defined
	 * on the given {@link Method}.
	 * @param <T> the {@link Annotation} type
	 * @param method the {@link Method}
	 * @param type the type
	 * @return the {@link Annotation}s
	 */
	public static <T extends Annotation>
		List<T> getAnnotations(Method method, Class<T> type) {
		List<T> ret = new ArrayList<T>();
		for (Annotation a : getAnnotations(method)) {
			if (type.isInstance(a)) {
				ret.add(type.cast(a));
			}
		}
		return ret;
	}

	/**
	 * Returns the first {@link Annotation} of the given type
	 * defined on the given {@link Method}.
	 * @param <T> the type
	 * @param method the method
	 * @param type the type of annotation
	 * @return the annotation or null
	 */
	public static <T extends Annotation>
		T getAnnotation(Method method, Class<T> type) {
		for (Annotation a : getAnnotations(method)) {
			if (type.isInstance(a)) {
				return type.cast(a);
			}
		}
		return null;
	}

	/**
	 * Returns the parameter {@link Annotation}s for the
	 * given {@link Method}.
	 * @param method the {@link Method}
	 * @return the {@link Annotation}s
	 */
	public static List<List<Annotation>> getParameterAnnotations(Method method) {
		if (methodParamAnnotationCache.containsKey(method)) {
			return methodParamAnnotationCache.get(method);
		}
		List<List<Annotation>> annotations = new ArrayList<List<Annotation>>();
		for (Annotation[] paramAnnotations : method.getParameterAnnotations()) {
			List<Annotation> listAnnotations = new ArrayList<Annotation>();
			for (Annotation a : paramAnnotations) {
				listAnnotations.add(a);
			}
			annotations.add(listAnnotations);
		}
		annotations = Collections.unmodifiableList(annotations);
		methodParamAnnotationCache.put(method, annotations);
		return annotations;
	}

	/**
	 * Returns the parameter {@link Annotation}s of the
	 * given type for the given {@link Method}.
	 * @param <T> the {@link Annotation} type
	 * @param type the type
	 * @param method the {@link Method}
	 * @return the {@link Annotation}s
	 */
	public static <T extends Annotation>
		List<List<T>> getParameterAnnotations(Method method, Class<T> type) {
		List<List<T>> annotations = new ArrayList<List<T>>();
		for (List<Annotation> paramAnnotations : getParameterAnnotations(method)) {
			List<T> listAnnotations = new ArrayList<T>();
			for (Annotation a : paramAnnotations) {
				if (type.isInstance(a)) {
					listAnnotations.add(type.cast(a));
				}
			}
			annotations.add(listAnnotations);
		}
		return annotations;
	}

}
