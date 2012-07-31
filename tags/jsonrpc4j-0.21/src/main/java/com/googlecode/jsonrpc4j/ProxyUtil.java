package com.googlecode.jsonrpc4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for create client proxies.
 */
public abstract class ProxyUtil {

	/**
	 * Creates a {@link Proxy} of the given {@link proxyInterface}
	 * that uses the given {@link JsonRpcClient}.
	 * @param <T> the proxy type
	 * @param classLoader the {@link ClassLoader}
	 * @param proxyInterface the interface to proxy
	 * @param client the {@link JsonRpcClient}
	 * @param ips the {@link InputStream}
	 * @param ops the {@link OutputStream}
	 * @return the proxied interface
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(
		ClassLoader classLoader,
		Class<T> proxyInterface,
		final JsonRpcClient client,
		final InputStream ips,
		final OutputStream ops) {

		// create and return the proxy
		return (T)Proxy.newProxyInstance(
			ClassLoader.getSystemClassLoader(),
			new Class<?>[] {proxyInterface},
			new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
					return client.invokeAndReadResponse(
						method.getName(), args, method.getGenericReturnType(), ops, ips);
				}
			});
	}

	/**
	 * Creates a {@link Proxy} of the given {@link proxyInterface}
	 * that uses the given {@link JsonRpcHttpClient}.
	 * @param <T> the proxy type
	 * @param classLoader the {@link ClassLoader}
	 * @param proxyInterface the interface to proxy
	 * @param client the {@link JsonRpcHttpClient}
	 * @param extraHeaders extra HTTP headers to be added to each response
	 * @return the proxied interface
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(
		ClassLoader classLoader,
		Class<T> proxyInterface,
		final JsonRpcHttpClient client,
		final Map<String, String> extraHeaders) {

		// create and return the proxy
		return (T)Proxy.newProxyInstance(
			classLoader,
			new Class<?>[] {proxyInterface},
			new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
					return client.invoke(
						method.getName(), args, method.getGenericReturnType(), extraHeaders);
				}
			});
	}

	/**
	 * Creates a {@link Proxy} of the given {@link proxyInterface}
	 * that uses the given {@link JsonRpcHttpClient}.
	 * @param <T> the proxy type
	 * @param classLoader the {@link ClassLoader}
	 * @param proxyInterface the interface to proxy
	 * @param client the {@link JsonRpcHttpClient}
	 * @return the proxied interface
	 */
	public static <T> T createProxy(
		ClassLoader classLoader,
		Class<T> proxyInterface,
		final JsonRpcHttpClient client) {
		return createProxy(classLoader, proxyInterface, client, new HashMap<String, String>());
	}

}
