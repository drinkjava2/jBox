/**
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.drinkjava2.jbeanbox;

/**
 * BeanBox Exception, transfer Exception to runtime type "BeanBoxException"
 * 
 * @author Yong Zhu
 * @since 2.4.2
 */
public class BeanBoxException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BeanBoxException() {
		// Default public constructor
	}

	public BeanBoxException(String message) {
		super(message);
	}

	public BeanBoxException(Throwable e) {
		super(e);
	}

	public BeanBoxException(String message, Throwable e) {
		super(message, e);
	}

	public static Object throwEX(String errorMsg, Exception e) {
		throw new BeanBoxException(errorMsg, e);
	}

	/**
	 * Transfer all Exceptions to RuntimeException SqlBoxException. The only place
	 * throw Exception in this project
	 */
	public static Object throwEX(String errorMsg) {
		return new BeanBoxException(errorMsg);
	}

	/**
	 * Eat exception to avoid SONAR warning
	 */
	public static Object eatException(Exception e) {// NOSONAR
		return null;
	}

}
