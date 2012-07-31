package com.googlecode.jsonrpc4j.spring;

public class Assertions {
	
	public static final int GENERIC_ERROR_CODE = 500;
	
	public static void isTrue(
		boolean bool, int code, String message, Object data) {
		if (!bool) {
			throw new JsonServiceException(code, message, data);
		}
	}
	
	public static void isTrue(
		boolean bool, int code, String message) {
		isTrue(bool, code, message, null);
	}
	
	public static void isTrue(
		boolean bool, int code) {
		isTrue(bool, code, null, null);
	}
	
	public static void isTrue(
		boolean bool, String message) {
		isTrue(bool, GENERIC_ERROR_CODE, message, null);
	}
	
	public static void isFalse(
		boolean bool, int code, String message, Object data) {
		isTrue(!bool, code, message, null);
	}
	
	public static void isFalse(
		boolean bool, int code, String message) {
		isTrue(!bool, code, message, null);
	}
	
	public static void isFalse(
		boolean bool, int code) {
		isTrue(!bool, code, null, null);
	}
	
	public static void isFalse(
		boolean bool, String message) {
		isTrue(!bool, GENERIC_ERROR_CODE, message, null);
	}
	
	public static void notNull(
		Object val, int code, String message, Object data) {
		isTrue(val!=null, code, message, null);
	}
	
	public static void notNull(
		Object val, int code, String message) {
		isTrue(val!=null, code, message, null);
	}
	
	public static void notNull(
		Object val, int code) {
		isTrue(val!=null, code, null, null);
	}
	
	public static void notNull(
		Object val, String message) {
		isTrue(val!=null, GENERIC_ERROR_CODE, message, null);
	}
	
}
