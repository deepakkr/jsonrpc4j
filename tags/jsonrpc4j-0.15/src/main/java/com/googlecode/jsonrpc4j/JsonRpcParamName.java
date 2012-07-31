package com.googlecode.jsonrpc4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for annotating service parameters as
 * JsonRpc params by name.
 * 
 * @author brian.dilley@gmail.com
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpcParamName {
    
    /**
     * The value of the parameter name.
     * @return the value
     */
    String value();
    
}
