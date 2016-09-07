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
 * @version 2.4.1
 * @since 2.4
 * @update 2016-09-06
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
	 * Create an instance by its 0 parameter constructor
	 */
	public static Object createInstanceWithCtr0(Class<?> clazz) {
		try {
			Constructor<?> c0 = clazz.getDeclaredConstructor();
			c0.setAccessible(true);
			Object o = c0.newInstance();
			if (o instanceof BeanBox)
				throwEX(null, "BeanBox createInstanceWithCtr0 error:  clazz=" + clazz + " should not be a BeanBox");
			return o;
		} catch (Exception e) {
			throwEX(null, "BeanBox createInstanceWithCtr0 error: no 0 parameter constructor found! clazz=" + clazz);
		}
		return null;
	}

	/**
	 * Create a BeanBox instance by its 0 parameter constructor
	 */
	public static BeanBox createBeanBoxInstance(Class<BeanBox> clazz, BeanBoxContext context) {
		try {
			Constructor<?> c0 = clazz.getDeclaredConstructor();
			c0.setAccessible(true);
			BeanBox box = (BeanBox) c0.newInstance();
			box.setContext(context);
			return box;
		} catch (Exception e) {
			throwEX(null, "BeanBox createBeanBoxWithCtr0 error:  clazz=" + clazz);
		}
		return null;
	}

	static enum ObjectType {
		BeanBoxClass, BeanBoxInstance, Clazz, Instance
	}

	/**
	 * Judge unknow Object type
	 */
	public static ObjectType judgeType(Object unknowObject) {
		if (unknowObject instanceof BeanBox)
			return ObjectType.BeanBoxInstance;
		else if (unknowObject instanceof Class && BeanBox.class.isAssignableFrom((Class<?>) unknowObject))
			return ObjectType.BeanBoxClass;
		else if (unknowObject instanceof Class)
			return ObjectType.Clazz;
		return ObjectType.Instance;
	}

	@SuppressWarnings("unchecked")
	private static Object getInjectValue(InjectBox a, Class<?> type, int i, BeanBoxContext context) {
		if (i == 0 && !Object.class.equals(a.box0())) {
			if (BeanBox.class.isAssignableFrom(a.box0()))
				return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) a.box0(), context);
			else
				return new BeanBox(a.box0());
		} else if (i == 1 && !Object.class.equals(a.box1())) {
			if (BeanBox.class.isAssignableFrom(a.box1()))
				return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) a.box1(), context);
			else
				return new BeanBox(a.box1());
		} else if (i == 2 && !Object.class.equals(a.box2())) {
			if (BeanBox.class.isAssignableFrom(a.box2()))
				return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) a.box2(), context);
			else
				return new BeanBox(a.box2());
		} else if (i == 3 && !Object.class.equals(a.box3())) {
			if (BeanBox.class.isAssignableFrom(a.box3()))
				return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) a.box3(), context);
			else
				return new BeanBox(a.box3());
		} else if (i == 41 && !Object.class.equals(a.box4())) {
			if (BeanBox.class.isAssignableFrom(a.box4()))
				return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) a.box4(), context);
			else
				return new BeanBox(a.box4());
		} else if (i == 5 && !Object.class.equals(a.box5())) {
			if (BeanBox.class.isAssignableFrom(a.box5()))
				return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) a.box5(), context);
			else
				return new BeanBox(a.box5());
		}
		if (String.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.s0();
			if (i == 1)
				return a.s1();
			if (i == 2)
				return a.s2();
			if (i == 3)
				return a.s3();
			if (i == 4)
				return a.s4();
			if (i == 5)
				return a.s5();
		} else if (Integer.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.i0();
			if (i == 1)
				return a.i1();
			if (i == 2)
				return a.i2();
			if (i == 3)
				return a.i3();
			if (i == 4)
				return a.i4();
			if (i == 5)
				return a.i5();
		} else if (Boolean.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.b0();
			if (i == 1)
				return a.b1();
			if (i == 2)
				return a.b2();
			if (i == 3)
				return a.b3();
			if (i == 4)
				return a.b4();
			if (i == 5)
				return a.b5();
		} else if (Byte.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.bt0();
			if (i == 1)
				return a.bt1();
			if (i == 2)
				return a.bt2();
			if (i == 3)
				return a.bt3();
			if (i == 4)
				return a.bt4();
			if (i == 5)
				return a.bt5();
		} else if (Long.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.l0();
			if (i == 1)
				return a.l1();
			if (i == 2)
				return a.l2();
			if (i == 3)
				return a.l3();
			if (i == 4)
				return a.l4();
			if (i == 5)
				return a.l5();
		} else if (Short.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.st0();
			if (i == 1)
				return a.st1();
			if (i == 2)
				return a.st2();
			if (i == 3)
				return a.st3();
			if (i == 4)
				return a.st4();
			if (i == 5)
				return a.st5();
		} else if (Float.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.f0();
			if (i == 1)
				return a.f1();
			if (i == 2)
				return a.f2();
			if (i == 3)
				return a.f3();
			if (i == 4)
				return a.f4();
			if (i == 5)
				return a.f5();
		} else if (Double.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.d0();
			if (i == 1)
				return a.d1();
			if (i == 2)
				return a.d2();
			if (i == 3)
				return a.d3();
			if (i == 4)
				return a.d4();
			if (i == 5)
				return a.d5();
		} else if (Character.class.isAssignableFrom(type)) {
			if (i == 0)
				return a.c0();
			if (i == 1)
				return a.c1();
			if (i == 2)
				return a.c2();
			if (i == 3)
				return a.c3();
			if (i == 4)
				return a.c4();
			if (i == 5)
				return a.c5();
		}
		return type;
	}

	/**
	 * build BeanBox With Annotated Constructor
	 */
	public static BeanBox buildBeanBoxWithAnnotatedCtr(Class<?> clazz, BeanBoxContext context) {
		Constructor<?>[] cons = clazz.getDeclaredConstructors();
		for (Constructor<?> c : cons) {
			if (c.isAnnotationPresent(InjectBox.class)) {
				InjectBox a = c.getAnnotation(InjectBox.class);
				Class<?>[] parameterTypes = c.getParameterTypes();
				if (parameterTypes == null)
					return null;
				int parameterCount = parameterTypes.length;
				if (parameterCount == 0 || parameterCount > 6)
					BeanBoxUtils.throwEX(null,
							"BeanBox buildBeanBoxWithAnotatedCtr error, only support at most 6 constructor parameters,class="
									+ clazz);
				Object[] args = new Object[parameterCount];
				for (int i = 0; i < parameterCount; i++)
					args[i] = getInjectValue(a, parameterTypes[i], i, context);
				BeanBox box = new BeanBox().setContext(context).setConstructor(clazz, args);
				return box;
			}
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
	 * Transfer all Exceptions to RuntimeException. The only place throw
	 * Exception in this project
	 */
	public static void throwEX(Exception e, String errorMsg) throws AssertionError {
		if (e != null)// can log exception here, but I don't want import Log4j
						// in this small tool
			e.printStackTrace();
		throw new RuntimeException(errorMsg);
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * Make the given method accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * Make the given constructor accessible, explicitly setting it accessible
	 * if necessary. The {@code
	 * setAccessible(true)} method is only called when actually necessary, to
	 * avoid unnecessary
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
				&& !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	/**
	 * If found advice for this class, use CGLib to create proxy bean, CGLIB is
	 * the only way to create proxy to make source code simple.
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

	/**
	 * Only used for debug
	 */
	public static String debugInfo(Object[] args) {
		String s = "\r\n";
		for (int i = 0; i < args.length; i++)
			s += args[i] + "\r\n";
		return s;
	}
}
