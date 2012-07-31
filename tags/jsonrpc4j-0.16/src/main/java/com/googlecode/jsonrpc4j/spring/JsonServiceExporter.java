package com.googlecode.jsonrpc4j.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

import com.googlecode.jsonrpc4j.JsonRpcServer;

/**
 * {@link RemoteExporter} that exports services using Json
 * according to the JSON-RPC proposal specified at:
 * <a href="http://groups.google.com/group/json-rpc">
 * http://groups.google.com/group/json-rpc</a>.
 * 
 * @author brian.dilley@gmail.com
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
    private boolean rethrowExceptions = false;

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

		// create the server
		jsonRpcServer = new JsonRpcServer(
			objectMapper, getService(), getServiceInterface());
		jsonRpcServer.setRethrowExceptions(rethrowExceptions);
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
	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * @param rethrowExceptions the rethrowExceptions to set
	 */
	public void setRethrowExceptions(boolean rethrowExceptions) {
		this.rethrowExceptions = rethrowExceptions;
	}

}
