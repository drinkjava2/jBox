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
package com.github.drinkjava2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This Reflection Utils, copied some source code from http://931360439-qq-com.iteye.com/blog/938886 and Spring project
 *
 * @author Yong Zhu
 * @since 2.4.2
 */
public class ReflectionUtils {

	private ReflectionUtils() {
		// Hide default constructor
	}

	/**
	 * Get class's Declared Constructor
	 */
	public static Constructor<?> getDeclaredConstructor(Class<?> clazz, Class<?>... parameterTypes) {// NOSONAR
		Constructor<?> ctr;
		try {
			ctr = clazz.getDeclaredConstructor(parameterTypes);
			return ctr;
		} catch (Exception e) {
			BeanBoxException.eatException(e);
		}
		return null;
	}

	/**
	 * Get object's Declared Constructor, if not found or exception happen return null
	 */
	public static Constructor<?> getDeclaredConstructor(Object object, Class<?>... parameterTypes) {
		if (object == null)
			return null;
		return getDeclaredConstructor(object.getClass(), parameterTypes);
	}

	/**
	 * Get Bean or its parent's method, if not found return null
	 */
	public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		Method method;
		for (Class<?> claz = clazz; claz != null && claz != Object.class; claz = claz.getSuperclass()) {
			try {
				method = claz.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {
				BeanBoxException.eatException(e);
			}
		}
		return null;
	}

	/**
	 * Get object or its parent class's method, if not found or exception happen return null
	 */
	public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
		if (object == null)
			return null;
		return getDeclaredMethod(object.getClass(), methodName, parameterTypes);
	}

	/**
	 * Invoke Bean or its parent's method
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
			Object[] parameters) {
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		makeAccessible(method);
		try {
			return method.invoke(object, parameters);
		} catch (Exception e) {
			BeanBoxException.throwEX(e,
					"BeanBox ReflectionUtils invokeMethod error, Object=" + object + ", methodName=" + methodName);
		}
		return null;
	}

	/**
	 * Get class or its parent's declared field, if not found return null
	 */
	public static Field getDeclaredField(Class<?> clazz, String fieldName) {
		Field field;
		for (Class<?> clz = clazz; clz != null && clz != Object.class; clz = clz.getSuperclass()) {
			try {
				field = clz.getDeclaredField(fieldName);
				return field;
			} catch (Exception e) {
				BeanBoxException.eatException(e);
			}
		}
		return null;
	}

	/**
	 * Get object or its parent class's declared field, if not found or exception happen return null
	 */
	public static Field getDeclaredField(Object object, String fieldName) {
		if (object == null)
			return null;
		return getDeclaredField(object.getClass(), fieldName);
	}

	/**
	 * Set Bean or its parent's field value
	 */
	public static void setFieldValue(Object object, String fieldName, Object value) {
		Field field = getDeclaredField(object, fieldName);
		makeAccessible(field);
		try {
			field.set(object, value);
		} catch (Exception e) {
			BeanBoxException.throwEX(e,
					"BeanBox ReflectionUtils setFieldValue error, Object=" + object + ", fieldName=" + fieldName);
		}
	}

	/**
	 * Direct read Bean or its parent's field value, ignore private/protected limitation, pass by getter
	 */
	public static Object getFieldValue(Object object, String fieldName) {
		Field field = getDeclaredField(object, fieldName);
		makeAccessible(field);
		try {
			return field.get(object);
		} catch (Exception e) {
			BeanBoxException.throwEX(e,
					"BeanBox ReflectionUtils getFieldValue error, Object=" + object + ", fieldName=" + fieldName);
		}
		return null;
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if necessary. The {@code setAccessible(true)}
	 * method is only called when actually necessary, to avoid unnecessary conflicts with a JVM SecurityManager (if
	 * active).
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * Make the given method accessible, explicitly setting it accessible if necessary. The {@code setAccessible(true)}
	 * method is only called when actually necessary, to avoid unnecessary conflicts with a JVM SecurityManager (if
	 * active).
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * Make the given constructor accessible, explicitly setting it accessible if necessary. The {@code
	 * setAccessible(true)} method is only called when actually necessary, to avoid unnecessary
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
				&& !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

}