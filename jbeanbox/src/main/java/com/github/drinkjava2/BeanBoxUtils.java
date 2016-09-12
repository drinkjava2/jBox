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
	 * Translate object[] to Object[] but replace BeanBox to bean instance, for invoke use
	 */
	public static Object[] getObjectRealValue(BeanBoxContext context, Object... beanArgs) {
		Object[] objects = new Object[beanArgs.length];
		for (int i = 0; i < objects.length; i++)
			objects[i] = BeanBoxUtils.getRealValue(beanArgs[i], context);
		return objects;
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
	public static Object getRealValue(Object unknow, BeanBoxContext context) {
		ObjectType type = BeanBoxUtils.judgeType(unknow);
		switch (type) {
		case BeanBoxInstance:
			return ((BeanBox) unknow).setContext(context).getBean();
		case BeanBoxClass:
			return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) unknow, context).getBean();
		case Clazz:
			return context.getBean((Class<?>) unknow);
		case Instance:
			return unknow;
		}
		return null;
	}

	/**
	 * Get annotated BeanBox instance, detail see InjectBox.java
	 */
	public static Object getInjectFieldValue(Class<?> ownerClass, InjectBox a, Class<?> fieldClass, String fieldname,
			int i, BeanBoxContext context) {
		Class<?> box = null;
		if (i == 0 && !Object.class.equals(a.value()))
			box = a.value();
		else if (i == 0 && !Object.class.equals(a.box0()))
			box = a.box0();
		else if (i == 1 && !Object.class.equals(a.box1()))
			box = a.box1();
		else if (i == 2 && !Object.class.equals(a.box2()))
			box = a.box2();
		else if (i == 3 && !Object.class.equals(a.box3()))
			box = a.box3();
		else if (i == 4 && !Object.class.equals(a.box4()))
			box = a.box4();
		else if (i == 5 && !Object.class.equals(a.box5()))
			box = a.box5();
		if (box != null)
			return getBeanBox(ownerClass, fieldClass, box, fieldname, context, true);

		if (i == 0 && !Object.class.equals(a.pox0()))
			box = a.pox0();
		else if (i == 1 && !Object.class.equals(a.pox1()))
			box = a.pox1();
		else if (i == 2 && !Object.class.equals(a.pox2()))
			box = a.pox2();
		else if (i == 3 && !Object.class.equals(a.pox3()))
			box = a.pox3();
		else if (i == 4 && !Object.class.equals(a.pox4()))
			box = a.pox4();
		else if (i == 5 && !Object.class.equals(a.pox5()))
			box = a.pox5();
		if (box != null)
			return getBeanBox(ownerClass, fieldClass, box, fieldname, context, true).setPrototype(true);

		if (i == 0 && !Object.class.equals(a.sox0()))
			box = a.sox0();
		else if (i == 1 && !Object.class.equals(a.sox1()))
			box = a.sox1();
		else if (i == 2 && !Object.class.equals(a.sox2()))
			box = a.sox2();
		else if (i == 3 && !Object.class.equals(a.sox3()))
			box = a.sox3();
		else if (i == 4 && !Object.class.equals(a.sox4()))
			box = a.sox4();
		else if (i == 5 && !Object.class.equals(a.sox5()))
			box = a.sox5();
		if (box != null)
			return getBeanBox(ownerClass, fieldClass, box, fieldname, context, true).setPrototype(false);

		BeanBox bx = getBeanBox(ownerClass, fieldClass, null, fieldname, context, true);
		if (bx != null)
			return bx;

		String methodname = null;
		if (String.class.isAssignableFrom(fieldClass))
			methodname = "s" + i;
		else if (Integer.class.isAssignableFrom(fieldClass))
			methodname = "i" + i;
		else if (Boolean.class.isAssignableFrom(fieldClass))
			methodname = "bl" + i;
		else if (Byte.class.isAssignableFrom(fieldClass))
			methodname = "bt" + i;
		else if (Long.class.isAssignableFrom(fieldClass))
			methodname = "l" + i;
		else if (Short.class.isAssignableFrom(fieldClass))
			methodname = "st" + i;
		else if (Float.class.isAssignableFrom(fieldClass))
			methodname = "f" + i;
		else if (Double.class.isAssignableFrom(fieldClass))
			methodname = "d" + i;
		else if (Character.class.isAssignableFrom(fieldClass))
			methodname = "c" + i;
		if (methodname != null)
			try {
				return InjectBox.class.getMethod(methodname).invoke(a);
			} catch (Exception e) {
				BeanBoxUtils.throwEX(e, "BeanBox getInjectFieldValue error, method" + methodname + "in fieldClass="
						+ fieldClass + " not exist");
			}
		return null;
	}

	/**
	 * build BeanBox With Annotated Constructor
	 */
	public static Object buildBeanBoxWithAnnotatedCtr(Class<?> clazz, BeanBoxContext context) {
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
					args[i] = getInjectFieldValue(clazz, a, parameterTypes[i], null, i, context);

				Object instance;
				try {
					instance = c.newInstance(getObjectRealValue(context, args));
					return instance;
				} catch (Exception e) {
					BeanBoxUtils.throwEX(e, "BeanBox buildBeanBoxWithAnnotatedCtr error, clazz=" + clazz);
				}
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
	 * Transfer all Exceptions to RuntimeException. The only place throw Exception in this project
	 */
	public static void throwEX(Exception e, String errorMsg) throws AssertionError {
		if (e != null)
			e.printStackTrace();
		throw new RuntimeException(errorMsg);
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

	protected static boolean isPrimitiveType(Class<?> fieldClass) {
		return ((String.class.equals(fieldClass) || Integer.class.equals(fieldClass) || Boolean.class.equals(fieldClass)
				|| Byte.class.equals(fieldClass) || Long.class.equals(fieldClass) || Short.class.equals(fieldClass)
				|| Float.class.equals(fieldClass) || Double.class.equals(fieldClass)
				|| Character.class.equals(fieldClass)));
	}

	/**
	 * Find BeanBox class and create BeanBox instance, for field with @InjectBox annotation, follow below order: <br/>
	 * Format: A.class{ @Inject(B.class) C fieldname;} <br/>
	 * 1) B.class (if is BeanBox)<br/>
	 * 2) B$CBox.class in B.class <br/>
	 * 3) B$FieldnameBox.class in B.class <br/>
	 * 
	 * Format: A.class{ @Inject C field; ...} <br/>
	 * 4) C.class (if is BeanBox)<br/>
	 * 5) CBox.class in same package of C <br/>
	 * 6) C$CBox.class in C.class <br/>
	 * 7) "ABox$CBox.class" in ABox.class <br/>
	 * 8) "ABox$FieldnameBox.class" in ABox.class <br/>
	 * 9) ConfigClass$CBox.class in globalConfig classes <br/>
	 * 10) ConfigClass$FieldnameBox.class in globalConfig classes <br/>
	 * 
	 * for a context.getBean(C.class) call, follow above #4, #5, #6, #9 order <br/>
	 * 
	 * If no BeanBox class found, if A.class has 0 parameter constructor or annotated constructor, wrap to BeanBox.<br/>
	 * if no BeanBox created at final, throw a error unless "required=false" set in @injectBox annotation
	 */
	@SuppressWarnings("unchecked")
	public static BeanBox getBeanBox(Class<?> ownerClass, Class<?> fieldClass, Class<?> annotationClass,
			String fieldName, BeanBoxContext context, boolean required) {
		if (Object.class.equals(annotationClass))
			annotationClass = null;
		Class<?> box = null;
		if (annotationClass != null) { // getBeanBox(A.class, B.class)
			if (BeanBox.class.isAssignableFrom(annotationClass))
				box = annotationClass;// #1
			if (box == null && fieldClass != null)
				box = BeanBoxUtils.checkIfExist(
						annotationClass.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// #2
			if (box == null)
				box = BeanBoxUtils.checkIfExist(annotationClass.getName() + "$"
						+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + context.boxIdentity);// #3
		} else {// getBeanBox(A.class)
			if (fieldClass == null)
				BeanBoxUtils.throwEX(null, "BeanBox getBeanBox error! target class not set");
			if (BeanBox.class.isAssignableFrom(fieldClass))
				box = fieldClass;
			if (box == null)
				box = BeanBoxUtils.checkIfExist(fieldClass.getName() + context.boxIdentity);// #5
			if (box == null)
				box = BeanBoxUtils
						.checkIfExist(fieldClass.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// #6
			if (box == null && ownerClass != null)
				box = BeanBoxUtils
						.checkIfExist(ownerClass.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// #6.5
			if (box == null && ownerClass != null)
				box = BeanBoxUtils.checkIfExist(ownerClass.getName() + context.boxIdentity + "$"
						+ fieldClass.getSimpleName() + context.boxIdentity);// #7

			if (box == null && ownerClass != null && !BeanBoxUtils.isEmptyStr(fieldName))
				box = BeanBoxUtils.checkIfExist(
						ownerClass.getName() + context.boxIdentity + "$" + fieldName + context.boxIdentity);// #8
			if (box == null) {
				for (Class<?> configs : context.configClassList) {
					box = BeanBoxUtils
							.checkIfExist(configs.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// #9
					if (box != null)
						break;
					if (!BeanBoxUtils.isEmptyStr(fieldName))
						box = BeanBoxUtils
								.checkIfExist(configs.getName() + "$" + fieldName.substring(0, 1).toUpperCase()
										+ fieldName.substring(1) + context.boxIdentity);// #10
					if (box != null)
						break;
				}
			}
		}
		BeanBox beanbox;
		if ((box == null) && BeanBoxUtils.isPrimitiveType(fieldClass))
			return null;
		if (box == null) {
			beanbox = new BeanBox(fieldClass).setContext(context); // try wrap it to a BeanBox
			if (!context.ignoreAnnotation) {
				InjectBox in = fieldClass.getAnnotation(InjectBox.class);
				if (in != null && in.prototype() == true)
					beanbox.setPrototype(true);
			}
		} else
			beanbox = BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) box, context);
		if (required && beanbox == null)
			BeanBoxUtils.throwEX(null, "BeanBox getBeanBox error! class can not be created, class=" + fieldClass);
		if (beanbox != null && beanbox.getClassOrValue() == null)
			beanbox.setClassOrValue(fieldClass);
		return beanbox;
	}
}
