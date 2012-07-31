package com.googlecode.jsonrpc4j;

import java.lang.reflect.Method;
import java.util.List;

import org.codehaus.jackson.JsonNode;

/**
 * {@link ErrorResolver} that uses annotations.
 */
public class AnnotationsErrorResolver
	implements ErrorResolver {

	/**
	 * {@inheritDoc}
	 */
	public JsonError resolveError(Throwable t, Method method, List<JsonNode> arguments) {

		// use annotations to map errors
		JsonRpcErrors errors = ReflectionUtil.getAnnotation(method, JsonRpcErrors.class);
		if (errors!=null) {
			for (JsonRpcError em : errors.value()) {
				if (em.exception().isInstance(t)) {
					String data = (em.data()!=null && em.data().trim().length()>0)
						? em.data() : t.getMessage();
					return new JsonError(em.code(), em.message(), data);
				}
			}
		}

		//  none found
		return null;
	}

}
