/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jbeanbox;

/**
 * BeanException for jBeanBox
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class BeanException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BeanException() {
		// Default constructor
	}

	public BeanException(String message) {
		super(message);
	}

	public BeanException(Throwable cause) {
		super(cause);
	}

	public BeanException(String message, Throwable cause) {
		super(message, cause);
	}

	public static <T> T throwEX(String errorMsg, Throwable e) {
		throw new BeanException(errorMsg, e);
	}

	public static <T> T throwEX(Throwable cause) {
		throw new BeanException(cause);
	}

	public static <T> T throwEX(String errorMsg) {
		throw new BeanException(errorMsg);
	}

	public static boolean assureNotNull(Object obj, String... optionMessages) {
		if (obj == null)
			throw new BeanException(
					optionMessages.length == 0 ? "Assert error, Object parameter can not be null" : optionMessages[0]);
		return true;
	}

	public static boolean assureNotEmpty(String str, String... optionMessages) {
		if (str == null || str.length() == 0)
			throw new BeanException(
					optionMessages.length == 0 ? "Assert error, String parameter can not be empty" : optionMessages[0]);
		return true;
	}
	
	public static boolean assure(boolean condition, String... optionMessages) {
		if (!condition)
			throw new BeanException(
					optionMessages.length == 0 ? "Assert expected true but got false" : optionMessages[0]);
		return true;
	}
}
