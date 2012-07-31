package com.googlecode.jsonrpc4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for holding an array of @JsonRpcError annotations
 * for a method.
 * 
 * @author Hans Jørgen Hoel (hansjorgen.hoel@nhst.no)
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpcErrors {

	JsonRpcError[] value();
}
