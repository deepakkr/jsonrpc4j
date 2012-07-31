package com.googlecode.jsonrpc4j.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
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
	
    public static final String JSONRPC_RESPONSE_CONTENT_TYPE = "application/json-rpc";
    public static final String[] JSONRPC_REQUEST_CONTENT_TYPES = {
        "application/json-rpc",
        "application/json",
        "application/jsonrequest"
    };

    private ObjectMapper objectMapper;
    private JsonRpcServer jsonRpcServer;
    private ApplicationContext applicationContext;

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
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, 
        IOException {

    	// get the servlet streams
    	ServletInputStream ips = request.getInputStream();
    	ServletOutputStream ops = response.getOutputStream();

    	// set response type
    	response.setContentType(JSONRPC_RESPONSE_CONTENT_TYPE);

    	// handle it
    	jsonRpcServer.handle(ips, ops);
    	ops.flush();
        
    }

    /**
     * {@inheritDoc}
     */
	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {
		this.applicationContext = applicationContext;
	}
    
}
