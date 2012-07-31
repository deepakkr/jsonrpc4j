package com.googlecode.jsonrpc4j;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A request and response object.
 */
public interface RequestAndResponse {

	/**
	 * Returns the {@link InputStream}.
	 * @return the The {@link InputStream}
	 */
	InputStream getInputStream();

	/**
	 * Returns the {@link OutputStream}.
	 * @return the The {@link OutputStream}
	 */
	OutputStream getOutputStream();

}
