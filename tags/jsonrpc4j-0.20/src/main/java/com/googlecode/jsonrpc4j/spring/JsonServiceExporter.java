package com.googlecode.jsonrpc4j.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

import com.googlecode.jsonrpc4j.AnnotationsErrorResolver;
import com.googlecode.jsonrpc4j.ErrorResolver;
import com.googlecode.jsonrpc4j.JsonRpcServer;

/**
 * {@link RemoteExporter} that exports services using Json
 * according to the JSON-RPC proposal specified at:
 * <a href="http://groups.google.com/group/json-rpc">
 * http://groups.google.com/group/json-rpc</a>.
 *
 */
public class JsonServiceExporter
	extends RemoteExporter
	implements HttpRequestHandler,
	InitializingBean,
	ApplicationContextAware {

	private ObjectMapper objectMapper;
	private JsonRpcServer jsonRpcServer;
	private ApplicationContext applicationContext;
	private ErrorResolver errorResolver = null;
	private boolean backwardsComaptible = true;
	private boolean rethrowExceptions = false;
	private boolean allowExtraParams = false;
	private boolean allowLessParams	= false;

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet()
		throws Exception {

		// find the ObjectMapper
		if (objectMapper==null
			&& applicationContext!=null
			&& applicationContext.containsBean("objectMapper")) {
			objectMapper = (ObjectMapper)applicationContext.getBean("objectMapper");
		}
		if (objectMapper==null
			&& applicationContext!=null) {
			try {
				objectMapper = (ObjectMapper)BeanFactoryUtils.beanOfTypeIncludingAncestors(
					applicationContext, ObjectMapper.class);
			} catch(Exception e) {
				objectMapper = new ObjectMapper();
			}
		}

		// make sure we have an error resolver
		if (errorResolver==null) {
			errorResolver = new AnnotationsErrorResolver();
		}

		// create the server
		jsonRpcServer = new JsonRpcServer(
			objectMapper, getService(), getServiceInterface(), errorResolver);
		jsonRpcServer.setBackwardsComaptible(backwardsComaptible);
		jsonRpcServer.setRethrowExceptions(rethrowExceptions);
		jsonRpcServer.setAllowExtraParams(allowExtraParams);
		jsonRpcServer.setAllowLessParams(allowLessParams);
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
		IOException {
		jsonRpcServer.handle(request, response);
		response.getOutputStream().flush();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * @param errorResolver the errorResolver to set
	 */
	public void setErrorResolver(ErrorResolver errorResolver) {
		this.errorResolver = errorResolver;
	}

	/**
	 * @param backwardsComaptible the backwardsComaptible to set
	 */
	public void setBackwardsComaptible(boolean backwardsComaptible) {
		this.backwardsComaptible = backwardsComaptible;
	}

	/**
	 * @param rethrowExceptions the rethrowExceptions to set
	 */
	public void setRethrowExceptions(boolean rethrowExceptions) {
		this.rethrowExceptions = rethrowExceptions;
	}

	/**
	 * @param allowExtraParams the allowExtraParams to set
	 */
	public void setAllowExtraParams(boolean allowExtraParams) {
		this.allowExtraParams = allowExtraParams;
	}

	/**
	 * @param allowLessParams the allowLessParams to set
	 */
	public void setAllowLessParams(boolean allowLessParams) {
		this.allowLessParams = allowLessParams;
	}

}
