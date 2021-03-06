package com.googlecode.jsonrpc4j.spring;

import static java.lang.String.format;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getAllInterfacesForClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.googlecode.jsonrpc4j.JsonRpcService;

/**
 * Auto exports {@link JsonRpcService} annotated beans as JSON-RPC services.
 * <p>
 * Minmizes the configuration necessary to export beans as JSON-RPC services to:
 * 
 * <pre>
 * &lt;bean class=&quot;com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceExporter&quot;/&gt;
 * 
 * &ltbean class="MyServiceBean"/>
 * </pre>
 */
public class AutoJsonRpcServiceExporter implements BeanFactoryPostProcessor {

  private static final Log LOG = LogFactory.getLog(AutoJsonRpcServiceExporter.class);

  private static final String PATH_PREFIX = "/";

  private Map<String, String> serviceBeanNames = new HashMap<String, String>();

  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
    findServiceBeanDefinitions(dlbf);
    for (Entry<String, String> entry : serviceBeanNames.entrySet()) {
      String servicePath = entry.getKey();
      String serviceBeanName = entry.getValue();
      registerServiceProxy(dlbf, makeUrlPath(servicePath), serviceBeanName);
    }
  }

  /**
   * Finds the beans to expose and puts them in the {@link #serviceBeanNames} map.
   * <p>
   * Searches parent factories as well.
   */
  private void findServiceBeanDefinitions(ConfigurableListableBeanFactory beanFactory) {
    for (String beanName : beanFactory.getBeanDefinitionNames()) {
      JsonRpcService jsonRpcPath = beanFactory.findAnnotationOnBean(beanName, JsonRpcService.class);
      if (jsonRpcPath != null) {
        String pathValue = jsonRpcPath.value();
        LOG.debug(format("Found JSON-RPC path '%s' for bean [%s].", pathValue, beanName));
        if (serviceBeanNames.containsKey(pathValue)) {
          String otherBeanName = serviceBeanNames.get(pathValue);
          LOG.warn(format("Duplicate JSON-RPC path specification: found %s on both [%s] and [%s].", pathValue, beanName, otherBeanName));
        }
        serviceBeanNames.put(pathValue, beanName);
      }
    }
    BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
    if (parentBeanFactory != null && ConfigurableListableBeanFactory.class.isInstance(parentBeanFactory)) {
      findServiceBeanDefinitions((ConfigurableListableBeanFactory) parentBeanFactory);
    }
  }
  
  /**
   * To make the {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping} export a bean automatically, the name should start with a '/'.
   */
  private String makeUrlPath(String servicePath) {
    return PATH_PREFIX.concat(servicePath);
  }

  /**
   * Registers the new beans with the bean factory.
   */
  private void registerServiceProxy(DefaultListableBeanFactory dlbf, String servicePath, String serviceBeanName) {
    BeanDefinition serviceBeanDefinition = findBeanDefintion(dlbf, serviceBeanName);
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JsonServiceExporter.class).addPropertyReference("service", serviceBeanName);
    for (Class<?> iface : getBeanInterfaces(serviceBeanDefinition, dlbf.getBeanClassLoader())) {
      if (iface.isAnnotationPresent(JsonRpcService.class)) {
        String serviceInterface = iface.getName();
        LOG.debug(format("Registering interface '%s' for JSON-RPC bean [%s].", serviceInterface, serviceBeanName));
        builder.addPropertyValue("serviceInterface", serviceInterface);
        break;
      }
    }
    dlbf.registerBeanDefinition(servicePath, builder.getBeanDefinition());
  }
  
  /**
   * Find a {@link BeanDefinition} in the {@link BeanFactory} or it's parents.
   */
  private BeanDefinition findBeanDefintion(ConfigurableListableBeanFactory beanFactory, String serviceBeanName) {
    if (beanFactory.containsLocalBean(serviceBeanName)) {
      return beanFactory.getBeanDefinition(serviceBeanName);
    }
    BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
    if (parentBeanFactory != null && ConfigurableListableBeanFactory.class.isInstance(parentBeanFactory)) {
      return findBeanDefintion((ConfigurableListableBeanFactory) parentBeanFactory, serviceBeanName);
    }
    throw new RuntimeException(format("Bean with name '%s' can no longer be found.", serviceBeanName));
  }

  private Class<?>[] getBeanInterfaces(BeanDefinition serviceBeanDefinition, ClassLoader beanClassLoader) {
    String beanClassName = serviceBeanDefinition.getBeanClassName();
    try {
      Class<?> beanClass = forName(beanClassName, beanClassLoader);
      return getAllInterfacesForClass(beanClass, beanClassLoader);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(format("Cannot find bean class '%s'.", beanClassName), e);
    } catch (LinkageError e) {
      throw new RuntimeException(format("Cannot find bean class '%s'.", beanClassName), e);
    }
  }

}
