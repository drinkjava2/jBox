/**
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.drinkjava2.jbeanbox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.drinkjava2.jbeanbox.springsrc.ReflectionUtils;

/**
 * Put lots miscellaneous public static methods here, have no time to organize
 * them
 * 
 * @author Yong Zhu
 * @since 2.4
 * 
 */
@SuppressWarnings("all")
public abstract class BeanBoxUtils {

	private static ConcurrentHashMap<String, Integer> classExistCache = new ConcurrentHashMap<String, Integer>();

	/**
	 * Return true if empty or null
	 */
	public static boolean isEmptyStr(String str) {
		return str == null || "".equals(str);
	}

	/**
	 * Check if class exist by search class name
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
			} catch (Exception e) {
				BeanBoxException.eatException(e);
				classExistCache.put(className, 0);
				return null;
			}
		if (1 == i) {
			try {
				return Class.forName(className);
			} catch (Exception e) {
				throw new BeanBoxException("Class '" + className + "' does not exist.");
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
				BeanBoxException
						.throwEX("BeanBox createInstanceWithCtr0 error:  clazz=" + clazz + " should not be a BeanBox");
			return o;
		} catch (Exception e) {
			BeanBoxException.eatException(e);
			return null;
		}
	}

	/**
	 * Translate object[] to Object[] but replace BeanBox to bean instance, for
	 * invoke use
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
			BeanBoxException.throwEX("BeanBox createBeanBoxWithCtr0 error:  clazz=" + clazz, e);
		}
		return null;
	}

	enum ObjectType {
		BEANBOX_CLASS, BEANBOX_INSTANCE, CLASS, INSTANCE
	}

	/**
	 * Judge unknow Object type
	 */
	public static ObjectType judgeType(Object unknowObject) {
		if (unknowObject instanceof BeanBox)
			return ObjectType.BEANBOX_INSTANCE;
		else if (unknowObject instanceof Class && BeanBox.class.isAssignableFrom((Class<?>) unknowObject))
			return ObjectType.BEANBOX_CLASS;
		else if (unknowObject instanceof Class)
			return ObjectType.CLASS;
		return ObjectType.INSTANCE;
	}

	@SuppressWarnings("unchecked")
	public static Object getRealValue(Object unknow, BeanBoxContext context) {
		ObjectType type = BeanBoxUtils.judgeType(unknow);
		switch (type) {
		case BEANBOX_INSTANCE:
			return ((BeanBox) unknow).setContext(context).getBean();
		case BEANBOX_CLASS:
			return BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) unknow, context).getBean();
		case CLASS:
			return context.getBean((Class<?>) unknow);
		case INSTANCE:
			return unknow;
		default:
			BeanBoxException.throwEX("BeanBoxUtils getRealValue default case error");
		}
		return null;
	}

	/**
	 * Get annotated BeanBox instance, detail see InjectBox.java
	 */
	public static Object getInjectFieldValue(Class<?> ownerClass, InjectBox a, Class<?> fieldClass, // NOSONAR
			String fieldname, int i, BeanBoxContext context) {
		Class<?> box = null;
		if (i == 1 && !Object.class.equals(a.value()))
			box = a.value();
		else if (i == 1 && !Object.class.equals(a.box()))
			box = a.box();
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
		else if (i == 6 && !Object.class.equals(a.box6()))
			box = a.box6();
		if (box != null)
			return getBeanBox(ownerClass, fieldClass, box, fieldname, context, true);

		if (i == 1 && !Object.class.equals(a.pox()))
			box = a.pox();
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
		else if (i == 6 && !Object.class.equals(a.pox6()))
			box = a.pox6();
		if (box != null)
			return getBeanBox(ownerClass, fieldClass, box, fieldname, context, true).setPrototype(true);

		if (i == 1 && !Object.class.equals(a.sox()))
			box = a.sox();
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
		else if (i == 6 && !Object.class.equals(a.sox6()))
			box = a.sox6();
		if (box != null)
			return getBeanBox(ownerClass, fieldClass, box, fieldname, context, true).setPrototype(false);

		BeanBox bx = getBeanBox(ownerClass, fieldClass, null, fieldname, context, true);
		if (bx != null)
			return bx;

		if ((i == 1) && !"".equals(a.s()))
			return a.s();

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
				return ReflectionUtils.findMethod(InjectBox.class, methodname).invoke(a);
			} catch (Exception e) {
				BeanBoxException.throwEX("BeanBox getInjectFieldValue error, method" + methodname + "in fieldClass="
						+ fieldClass + " not exist", e);
			}
		return null;
	}

	/**
	 * build BeanBox With Annotated Constructor
	 */
	public static Object buildBeanBoxWithAnnotatedCtr(Class<?> clazz, BeanBoxContext context) {// NOSONAR
		Constructor<?>[] cons = clazz.getDeclaredConstructors();
		for (Constructor<?> c : cons) {
			if (c.isAnnotationPresent(InjectBox.class)) {
				InjectBox anno = c.getAnnotation(InjectBox.class);
				Class<?>[] parameterTypes = c.getParameterTypes();
				if (parameterTypes == null)
					return null;
				int parameterCount = parameterTypes.length;
				if (parameterCount == 0 || parameterCount > 6)
					BeanBoxException.throwEX(
							"BeanBox buildBeanBoxWithAnotatedCtr error, only support at most 6 constructor parameters,class="
									+ clazz);
				Object[] args = new Object[parameterCount];
				for (int i = 0; i < parameterCount; i++)
					args[i] = getInjectFieldValue(clazz, anno, parameterTypes[i], null, i + 1, context);

				Object instance;
				try {
					instance = c.newInstance(getObjectRealValue(context, args));
					return instance;
				} catch (Exception e) {
					return BeanBoxException.eatException(e);
				}
			}
		}
		return null;
	}

	/**
	 * If found advice for this class, use CGLib to create proxy bean, CGLIB is the
	 * only way to create proxy in jBeanBox
	 */
	public static boolean ifHaveAdvice(BeanBox box, List<Advisor> advisors, Object classOrValue) {
		if (box.needCreateProxy != null) {
			if (box.needCreateProxy)
				return true;
			else
				return false;
		}
		if (classOrValue == null || !(classOrValue instanceof Class)) {
			box.needCreateProxy = false;
			return false;
		}
		Method[] methods = ((Class<?>) classOrValue).getMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(AopAround.class)) {// if have AopAround annotation
				box.needCreateProxy = true;
				return true;
			}

			// If have @TX, @Trans format self customised annotation
			if (!box.getContext().aopAroundAnnotationsMap.isEmpty()) {
				Annotation[] annos = m.getDeclaredAnnotations();
				if (annos != null)
					for (Annotation ano : annos) {
						for (Class<?> key : box.getContext().aopAroundAnnotationsMap.keySet())
							if (key.equals(ano.annotationType())) {
								box.needCreateProxy = true;
								return true;
							}
					}
			}

			for (Advisor adv : advisors)
				if (adv.match(((Class<?>) classOrValue).getName(), m.getName())) {
					box.needCreateProxy = true;
					return true;
				}
		}
		box.needCreateProxy = false;
		return false;
	}

	protected static boolean isPrimitiveType(Class<?> fieldClass) {
		return String.class.equals(fieldClass) || Integer.class.equals(fieldClass) || Boolean.class.equals(fieldClass)// NOSONAR
				|| Byte.class.equals(fieldClass) || Long.class.equals(fieldClass) || Short.class.equals(fieldClass)
				|| Float.class.equals(fieldClass) || Double.class.equals(fieldClass)
				|| Character.class.equals(fieldClass);
	}

	/**
	 * Find BeanBox class and create BeanBox instance, for field with @InjectBox
	 * annotation, follow below order: <br/>
	 * Format: A.class{ @Inject(B.class) C fieldname;} <br/>
	 * 1) B.class (if is BeanBox)<br/>
	 * 2) B$CBox.class in B.class <br/>
	 * 3) B$FieldnameBox.class in B.class <br/>
	 * 
	 * Format: A.class{ @Inject C fieldname; ...} <br/>
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
	 * If no BeanBox class found, if A.class has 0 parameter constructor or
	 * annotated constructor, wrap to BeanBox.<br/>
	 * if no BeanBox created at final, throw a error unless "required=false" set
	 * in @injectBox annotation
	 * 
	 * @param ownerClass
	 *            Optional, the owner class which have a field
	 * @param fieldClass
	 *            The target class or a field type where have an Inject annotation
	 *            marked on the field
	 * @param annotatinClass
	 *            Optional, the Inject annotation value marked on a field
	 * @param fieldName
	 *            Optional, the fieldName which have an Inject annotation marked on
	 *            it
	 * @param context
	 *            The BeanBoxContext instance
	 * @param required
	 *            If set true and no BeanBox found, throw an Exception, if set false
	 *            will not throw Exception
	 * @return The BeanBox instance
	 */
	@SuppressWarnings("unchecked")
	protected static BeanBox getBeanBox(Class<?> ownerClass, Class<?> fieldClass, Class<?> annotatinClass, // NOSONAR
			String fieldName, BeanBoxContext context, boolean required) {
		BeanBox beanbox = null;
		Class<?> annoClass = annotatinClass;
		if (Object.class.equals(annoClass))
			annoClass = null;
		Class<?> box = null;
		if (annoClass != null) { // getBeanBox(A.class, B.class)
			if (BeanBox.class.isAssignableFrom(annoClass))
				box = annoClass;// #1
			if (box == null && fieldClass != null)
				box = BeanBoxUtils
						.checkIfExist(annoClass.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// #2
			if (box == null)
				box = BeanBoxUtils.checkIfExist(annoClass.getName() + "$" + fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1) + context.boxIdentity);// #3
		} else {// getBeanBox(A.class)
			if (fieldClass == null)
				BeanBoxException.throwEX("BeanBox getBeanBox error! target class not set");
			if (BeanBox.class.isAssignableFrom(fieldClass))
				box = fieldClass;
			if (box == null)
				box = BeanBoxUtils.checkIfExist(fieldClass.getName() + context.boxIdentity);// NOSONAR #5
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
				for (Class<?> configs : context.getConfigClassList()) {// NOSONAR
					box = BeanBoxUtils
							.checkIfExist(configs.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// #9
					if (box != null)// NOSONAR
						break;
					if (!BeanBoxUtils.isEmptyStr(fieldName))// NOSONAR
						box = BeanBoxUtils
								.checkIfExist(configs.getName() + "$" + fieldName.substring(0, 1).toUpperCase()
										+ fieldName.substring(1) + context.boxIdentity);// #10
					if (box != null)// NOSONAR
						break;
				}
			}
		}
		if ((box == null) && BeanBoxUtils.isPrimitiveType(fieldClass))
			return null;
		if (box == null) {
			beanbox = new BeanBox(fieldClass).setContext(context); // try wrap it to a BeanBox
			if (!context.ignoreAnnotation) {
				InjectBox in = fieldClass.getAnnotation(InjectBox.class);// NOSONAR
				if (in != null && in.prototype())
					beanbox.setPrototype(true);
			}
		} else
			beanbox = BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) box, context);
		if (required && beanbox == null)
			BeanBoxException.throwEX("BeanBox getBeanBox error! class can not be created, class=" + fieldClass);
		if (beanbox != null && beanbox.getClassOrValue() == null)
			beanbox.setClassOrValue(fieldClass);
		return beanbox;
	}
	
	private static Map<Class<?>, Object[]> createMethodCache = new HashMap<Class<?>, Object[]>();

	protected static Method checkAndReturnCreateMethod(Class<?> clazz) {
		Object[] methods = createMethodCache.get(clazz);
		if (methods == null) {
			Method mtd = null;
			try {
				mtd = ReflectionUtils.findMethod(clazz, BeanBox.CREATE_METHOD);
			} catch (Exception e) {
			}
			if (mtd != null)
				createMethodCache.put(clazz, new Object[] { mtd });
			else
				createMethodCache.put(clazz, new Object[] {});
			return mtd;
		} else {
			if (methods.length == 1)
				return (Method) methods[0];
			else
				return null;
		}
	}
	
	private static Map<Class<?>, Object[]> cconfigMethodCache = new HashMap<Class<?>, Object[]>();

	protected static Method checkAndReturnConfigMethod(Class<?> clazz) {
		Object[] methods = cconfigMethodCache.get(clazz);
		if (methods == null) {
			Method mtd = null;
			try {
				mtd = ReflectionUtils.findMethod(clazz, BeanBox.CONFIG_METHOD);
			} catch (Exception e) {
			}
			if (mtd != null)
				cconfigMethodCache.put(clazz, new Object[] { mtd });
			else
				cconfigMethodCache.put(clazz, new Object[] {});
			return mtd;
		} else {
			if (methods.length == 1)
				return (Method) methods[0];
			else
				return null;
		}
	}
}
