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
package net.sf.jbeanbox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.cglib.proxy.Enhancer;

/**
 * Lots public static methods be moved into this BeanBoxUtils class
 * 
 * @author Yong Zhu
 * @since 2016-2-13
 * @update 2016-08-21
 *
 */
public class BeanBoxUtils {
	private static ConcurrentHashMap<String, Integer> classExistCache = new ConcurrentHashMap<String, Integer>();

	/**
	 * Return true if empty or null
	 */
	public static boolean isEmptyStr(String str) {
		return (str == null || "".equals(str));
	}

	/**
	 * Search class by name
	 */
	public static Class<?> checkIfExist(String className) {
		Integer i = classExistCache.get(className);
		if (i == null)
			try {
				Class<?> clazz = Class.forName(className);
				if (BeanBox.class.isAssignableFrom((Class<?>) clazz)) {
					classExistCache.put(className, 1);
					return clazz;
				}
				classExistCache.put(className, 0);
				return null;
			} catch (Throwable e) {
				classExistCache.put(className, 0);
				return null;
			}
		if (1 == i) {
			try {
				return Class.forName(className);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Create an instance by a class
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createBeanOrBoxInstance(Class<?> clazz, BeanBoxContext context) {
		try {
			Constructor<?> ctor[] = clazz.getDeclaredConstructors();
			for (Constructor<?> con : ctor) {
				Class<?> cx[] = con.getParameterTypes();
				if (cx.length == 0) {
					makeAccessible(con);
					Object o = con.newInstance();
					if (o instanceof BeanBox)
						((BeanBox) o).setContext(context);
					return (T) o;
				}
			}
			throwError(null,
					"BeanBox createBeanOrBoxInstance error: no 0 parameter constructor found! boxClass=" + clazz);
		} catch (Exception e) {
			throwError(e, "BeanBox createBeanOrBoxInstance error! boxClass=" + clazz);
		}
		return null;
	}

	/**
	 * Use CGLib create proxy bean, if advice set for this class
	 */
	public static Object getProxyBean(Class<?> clazz, CopyOnWriteArrayList<Advisor> advisorList) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new ProxyBean(clazz, advisorList));
		return enhancer.create();
	}

	/**
	 * Transfer all Exceptions to AssertionError. The only place throw Exception/Error in this project
	 */
	public static void throwError(Exception e, String errorMsg) throws AssertionError {
		if (e != null)
			e.printStackTrace();
		throw new AssertionError(errorMsg);
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
	 * Make the given constructor accessible, explicitly setting it accessible if necessary. The
	 * {@code setAccessible(true)} method is only called when actually necessary, to avoid unnecessary
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
				&& !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	/**
	 * If found advice for this class, use CGLib to create proxy bean, CGLIB is the only way to create proxy to make
	 * source code simple.
	 */
	public static boolean ifHaveAdvice(CopyOnWriteArrayList<Advisor> advisors, Object classOrValue) {
		if (classOrValue == null || !(classOrValue instanceof Class))
			return false;
		Method[] methods = ((Class<?>) classOrValue).getMethods();
		for (Method method : methods)
			for (Advisor adv : advisors)
				if (adv.match(((Class<?>) classOrValue).getName(), method.getName()))
					return true;
		return false;
	}
}
