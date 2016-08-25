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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

/**
 * jBeanBox is a micro scale IOC & AOP framework for Java 6 and above
 * 
 * @author Yong9981@gmail.com (Yong Zhu)
 * @since 2016-2-13
 * @version 2.3
 * @update 2016-07-20
 * 
 */
@SuppressWarnings("unchecked")
public class BeanBox {

	private static enum PropertyType {
		BEAN, VALUE, STATIC_FACTORY, BEAN_FACTORY
	}

	private Object classOrValue;// Class or an Object
	private boolean isValueType = false; // if true mean this bean is a value, not a bean, no need to create an instance
	// for it
	private boolean isSingleTon = true;// Default is SingleTon
	private Object[] constructorArgs;// for constructor injection use
	private String postConstructor;
	private String preDestory;
	private ConcurrentHashMap<String, Object[]> properties = new ConcurrentHashMap<String, Object[]>();// properties
	public static final BeanBoxContext defaultContext = new BeanBoxContext();// this is a global default context
	public BeanBoxContext context = defaultContext;

	// private static CopyOnWriteArrayList<Advisor> advisorList = new CopyOnWriteArrayList<Advisor>();// Advisors stored
	// in BeanBox's name space

	// ===========public static method begin========
	public static <T> T getBean(Class<?> clazz) {
		return defaultContext.getBean(clazz);
	}

	// ===========public static method end========

	// ==============Bean methods begin=============
	/**
	 * For a field with InjectBox annotation, find the BeanBox for it, there are 7 different cases
	 */
	private BeanBox findAnnotationFieldBox(Class<?> BeanClass, Class<?> annotationClass, Field field) {
		Class<?> fieldClass = field.getType();
		Class<?> box;
		if (!Object.class.equals(annotationClass)) {// @InjectBox(AnnotationClass.class)
			if (BeanBox.class.isAssignableFrom((Class<?>) annotationClass))
				box = annotationClass;// case 1
			else
				box = ifExistBeanBoxClass(
						annotationClass.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// case 2
			if (box == null)
				box = ifExistBeanBoxClass(
						annotationClass.getName() + "$" + field.getName().substring(0, 1).toUpperCase()
								+ field.getName().substring(1) + context.boxIdentity);// case 3
		} else {// @InjectBox
			box = ifExistBeanBoxClass(fieldClass.getName() + context.boxIdentity);// case 4
			if (box == null) {
				box = ifExistBeanBoxClass(BeanClass.getName() + context.boxIdentity + "$" + fieldClass.getSimpleName()
						+ context.boxIdentity);// case 5
				if (box == null) {
					for (Class<?> configs : context.configClassList) {
						box = ifExistBeanBoxClass(
								configs.getName() + "$" + fieldClass.getSimpleName() + context.boxIdentity);// case 6
						if (box != null)
							break;
					}
					if (box == null) {
						for (Class<?> configs : context.configClassList) {
							box = ifExistBeanBoxClass(
									configs.getName() + "$" + field.getName().substring(0, 1).toUpperCase()
											+ field.getName().substring(1) + context.boxIdentity);// case 7
							if (box != null)
								break;
						}
					}
				}
			}
		}
		if (box != null)
			return createBeanOrBoxInstance(box, context);
		return null;
	}

	// ===============BeanBox class Setter and Getter methods begin==================
	public BeanBox() {// Constructor for BeanBox
	}

	public BeanBox(Object classOrValue) {// Constructor for BeanBox
		setClassOrValue(classOrValue);
	}

	public BeanBox(BeanBoxContext context) {// Constructor for BeanBox with BeanBox Context
		this.context = context;
	}

	public BeanBox(Object classOrValue, BeanBoxContext context) {// Constructor for bean with constructor
		this.classOrValue = classOrValue;
		this.context = context;
	}

	public BeanBox(Class<?> constructorClass, Object... constructorParameters) {// Constructor for bean with constructor
		classOrValue = constructorClass;
		constructorArgs = constructorParameters;
	}

	public BeanBox setConstructor(Class<?> constructorClass, Object... constructorParameters) {
		classOrValue = constructorClass;
		constructorArgs = constructorParameters;
		return this;
	}

	public BeanBox setClassOrValue(Object classOrValue) {// if is class, need create a bean instance
		this.classOrValue = classOrValue;
		if (!(classOrValue instanceof Class))
			isValueType = true;
		return this;
	}

	public BeanBox setValueType(boolean isValueType) {// if set true, will not create class instance, simple return
		// classOrValue
		this.isValueType = isValueType;
		return this;
	}

	public Object getProperty(String property) {// get the property
		return (properties.get(property))[1];
	}

	public BeanBoxContext getContext() {
		return context;
	}

	public BeanBox setContext(BeanBoxContext context) {
		this.context = context;
		return this;
	}

	public boolean isSingleTon() {
		return isSingleTon;
	}

	public BeanBox setSingleTon(boolean isSingleTon) {// Default type is singleTon, all signleTon bean instance will be
		// cached
		this.isSingleTon = isSingleTon;
		return this;
	}

	public Object[] getConstructorArgs() {
		return constructorArgs;
	}

	public Object getClassOrValue() {
		return classOrValue;
	}

	public String getPostConstructor() {
		return postConstructor;
	}

	public BeanBox setPostConstructor(String postConstructor) {
		this.postConstructor = postConstructor;
		return this;
	}

	public String getPreDestory() {
		return preDestory;
	}

	public BeanBox setPreDestory(String preDestory) {
		this.preDestory = preDestory;
		return this;
	}

	/**
	 * Set property, can be BeanBox class or normal class or value, for normal class will be wrapped as BeanBox<br/>
	 */
	public BeanBox setProperty(String property, Object beanBoxInstanceOrValue) {
		if (beanBoxInstanceOrValue instanceof BeanBox)
			properties.put(property, new Object[] { PropertyType.BEAN, beanBoxInstanceOrValue });
		else if (beanBoxInstanceOrValue instanceof Class
				&& BeanBox.class.isAssignableFrom((Class<?>) beanBoxInstanceOrValue))
			try {
				properties.put(property, new Object[] { PropertyType.BEAN,
						createBeanOrBoxInstance((Class<?>) beanBoxInstanceOrValue, context) });
			} catch (Exception e) {
				printAndThrowError(e, "BeanBox setProperty error! property=" + property + " beanBoxInstanceOrValue="
						+ beanBoxInstanceOrValue);
			}
		else if (beanBoxInstanceOrValue instanceof Class)
			properties.put(property, new Object[] { PropertyType.BEAN, new BeanBox(beanBoxInstanceOrValue) });
		else
			properties.put(property, new Object[] { PropertyType.VALUE, beanBoxInstanceOrValue });
		return this;
	}

	public BeanBox setStaticFactory(String property, Class<?> staticFactoryClass, String methodName, Object... args) {// as
		// title
		properties.put(property, new Object[] { PropertyType.STATIC_FACTORY, staticFactoryClass, methodName, args });
		return this;
	}

	public BeanBox setBeanFactory(String property, BeanBox beanBox, String methodName, Object... args) {// as title
		properties.put(property, new Object[] { PropertyType.BEAN_FACTORY, beanBox, methodName, args });
		return this;
	}

	// ===============Setter and Getter methods end==================

	// ===================Create bean methods begin==================

	// Inject values into bean, use standard JDK reflection, bean setter methods are necessary
	private void invokeMethodToSetValue(Object bean, Method method, Object... args) {
		try {
			if (((PropertyType) args[0]) == PropertyType.VALUE) {
				method.invoke(bean, new Object[] { args[1] });
			} else if (((PropertyType) args[0]) == PropertyType.BEAN)
				method.invoke(bean, new Object[] { ((BeanBox) args[1]).setContext(context).getBean() });
			else if (((PropertyType) args[0]) == PropertyType.STATIC_FACTORY) {
				// PropertyType.STATIC_FACTORY, staticFactoryClass, methodName, args
				Class<?> c = (Class<?>) args[1];
				Object[] beanArgs = (Object[]) args[3];
				Method m = c.getMethod((String) args[2], getObjectClassType(beanArgs));
				Object beaninstance = m.invoke(c, getObjectRealValue(beanArgs));
				method.invoke(bean, new Object[] { beaninstance });
			} else if (((PropertyType) args[0]) == PropertyType.BEAN_FACTORY) {
				// PropertyType.BEAN_FACTORY, beanBox, methodName, args
				Object instance = ((BeanBox) args[1]).setContext(context).getBean();
				Object[] beanArgs = (Object[]) args[3];
				Method m = instance.getClass().getMethod((String) args[2], getObjectClassType(beanArgs));
				Object beaninstance = m.invoke(instance, getObjectRealValue(beanArgs));
				method.invoke(bean, new Object[] { beaninstance });
			}
		} catch (Exception e) {
			printAndThrowError(e,
					"BeanBox invokeMethodToSetValue error! bean=" + bean + " method=" + method + " args=" + args);
		}
	}

	// Inject values into bean, use field.set method, not recommend because it can inject into private field.
	private void forceInjectFieldValue(Object bean, Field field, Object... args) {
		try {
			makeAccessible(field);
			if (((PropertyType) args[0]) == PropertyType.VALUE) {
				field.set(bean, args[1]);
			} else if (((PropertyType) args[0]) == PropertyType.BEAN)
				field.set(bean, ((BeanBox) args[1]).setContext(context).getBean());
			else if (((PropertyType) args[0]) == PropertyType.STATIC_FACTORY) {
				Class<?> c = (Class<?>) args[1];
				Object[] beanArgs = (Object[]) args[3];
				Method m = c.getMethod((String) args[2], getObjectClassType(beanArgs));
				Object beaninstance = m.invoke(c, getObjectRealValue(beanArgs));
				field.set(bean, beaninstance);
			} else if (((PropertyType) args[0]) == PropertyType.BEAN_FACTORY) {
				Object instance = ((BeanBox) args[1]).setContext(context).getBean();
				Object[] beanArgs = (Object[]) args[3];
				Method m = instance.getClass().getMethod((String) args[2], getObjectClassType(beanArgs));
				Object beaninstance = m.invoke(instance, getObjectRealValue(beanArgs));
				field.set(bean, beaninstance);
			}
		} catch (Exception e) {
			printAndThrowError(e,
					"BeanBox invokeMethodToSetValue error! bean=" + bean + " field=" + field + " args=" + args);
		}
	}

	// Inject properties values into bean instance
	private void injectInstancePropertyValues(Object instance) {
		Method[] methods = instance.getClass().getDeclaredMethods();
		Set<String> keys = properties.keySet();
		for (String property : keys) {
			boolean found = false;
			for (Method method : methods) {
				String setter = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
				if (method.getName().equals(setter)) {
					invokeMethodToSetValue(instance, method, properties.get(property));
					found = true;
				}
			}
			if (!found) {
				try {
					Field field = instance.getClass().getDeclaredField(property);
					forceInjectFieldValue(instance, field, properties.get(property));
				} catch (Exception e) {
					printAndThrowError(e, "BeanBox setInstancePropertyValues error! class=" + this.getClassOrValue()
							+ ", property ='" + property
							+ "', this may caused by inject value into Proxy bean, it's not supported by CGLib");
				}
			}
		}
	}

	/**
	 * Inject BeanBox to a field with @Inject annotation <br/>
	 * 1) A.class{ @Inject(B.class) C c; ...} will find B.class as BeanBox class and inject to c, if B.class is not
	 * beanBox, will find static class "B.CBox.class" in B.class<br/>
	 * 2) A.class{ @Inject C c; ...} will find BeanBox CBox.class in same package of C, if not found, find
	 * "ABox.CBox.class" in ABox.class, if not found, find CBox in globalConfigs
	 */
	private void injectAnnotationFields(Class<?> beanClass, Object beanInstance) {
		Field[] fields = beanClass.getDeclaredFields();
		for (Field field : fields) {
			InjectBox injectAnnotation = field.getAnnotation(InjectBox.class);
			try {
				if (injectAnnotation != null) {
					BeanBox box = findAnnotationFieldBox(beanClass, injectAnnotation.value(), field);
					if (box == null && injectAnnotation.required())
						printAndThrowError(null, "BeanBox injectAnnotationFields required BeanBox not found! beanClass="
								+ beanClass + ", field name=" + field.getName());
					if (box == null)
						return;
					if (box.getClassOrValue() == null)
						box.setClassOrValue(field.getType());
					makeAccessible(field);
					field.set(beanInstance, box.getBean());
				}
			} catch (Exception e) {
				printAndThrowError(e,
						"BeanBox injectAnnotationFields error! beanClass=" + beanClass + " field=" + field.getName());
			}
		}
	}

	// Translate object[] to Class[], for invoke use
	private Class<?>[] getObjectClassType(Object... beanArgs) {
		Class<?>[] classes = new Class[beanArgs.length];
		for (int i = 0; i < classes.length; i++) {
			classes[i] = beanArgs[i].getClass();
			if (BeanBox.class.isAssignableFrom(classes[i])) {
				classes[i] = (Class<? extends Object>) (((BeanBox) beanArgs[i]).getClassOrValue());
			}
		}
		return classes;
	}

	// Translate object[] to Object[] but replace beanbox to bean instance, for invoke use
	private Object[] getObjectRealValue(Object... beanArgs) {
		Object[] objects = new Object[beanArgs.length];
		for (int i = 0; i < objects.length; i++) {
			if (beanArgs[i] instanceof BeanBox) {
				((BeanBox) beanArgs[i]).setContext(context);
				objects[i] = ((BeanBox) beanArgs[i]).getBean();
			} else
				objects[i] = beanArgs[i];
		}
		return objects;
	}

	/**
	 * Create bean instance or get singleTon bean instance in cache (if have)
	 */
	public <T> T getBean() {// create the bean or get singleTon bean from cache
		if (isValueType)
			return (T) classOrValue;
		String beanID = getClass().getName();// use beanBox class name as ID
		if (BeanBox.class.getName().equals(beanID)) {
			if (this.getClassOrValue() instanceof Class)// use real bean class name & args as bean ID
				beanID = ((Class<?>) this.getClassOrValue()).getName()
						+ (constructorArgs == null ? "" : constructorArgs);
			else
				printAndThrowError(null, "BeanBox createOrGetFromCache error! BeanBox ID can not be determined!");
		}
		Object instance = null;
		synchronized (context.signletonCache) {
			if (isSingleTon) {
				instance = context.signletonCache.get(beanID);
				if (instance != null)
					return (T) instance;// found singleTon bean in cache, good luck
			}
			if (ifHaveAdvice(context.advisorList, classOrValue)) {
				instance = getProxyBean((Class<?>) classOrValue, context.advisorList);// Proxy bean created
			} else if (constructorArgs != null)
				try {
					Class<?>[] argsTypes = getObjectClassType(constructorArgs);
					outer: for (Constructor<?> c : ((Class<?>) classOrValue).getConstructors()) {
						Class<?>[] cType = c.getParameterTypes();
						if (cType.length != argsTypes.length)
							continue outer;
						for (int i = 0; i < cType.length; i++)
							if (!cType[i].isAssignableFrom(argsTypes[i]))
								continue outer;
						instance = c.newInstance(getObjectRealValue(constructorArgs));
						break;
					}
					if (instance == null)
						printAndThrowError(null,
								"BeanBox call constructor error! not found match constructor for " + classOrValue);
				} catch (Exception e) {
					printAndThrowError(e, "BeanBox create constructor error! constructor=" + classOrValue);
				}
			else if (classOrValue instanceof Class) {
				try {
					// instance = ((Class) classOrValue).newInstance();// just new it
					instance = createBeanOrBoxInstance((Class<?>) classOrValue, context);
				} catch (Exception e) {
					printAndThrowError(e, "BeanBox create bean error! class=" + classOrValue);
				}
			} else
				printAndThrowError(null, "BeanBox create bean error! classOrValue=" + classOrValue);
			if (isSingleTon) {
				context.signletonCache.put(beanID, instance);// save in cache
				if (!isEmptyStr(this.getPreDestory())) {
					try {
						Method predestoryMethod = instance.getClass().getDeclaredMethod(getPreDestory(),
								new Class[] {});
						this.context.preDestoryMethodCache.put(beanID, predestoryMethod);
					} catch (Exception e) {
						printAndThrowError(e, "BeanBox  create bean error!  PreDestory=" + getPreDestory());
					}
				}
			}
		}
		if (instance == null)
			return null;
		injectAnnotationFields((Class<?>) classOrValue, instance);
		injectInstancePropertyValues(instance);
		if (!isEmptyStr(getPostConstructor()))
			try {
				Method postConstructor = instance.getClass().getDeclaredMethod(getPostConstructor(), new Class[] {});
				postConstructor.invoke(instance, new Object[] {});
			} catch (Exception e) {
				printAndThrowError(e, "BeanBox  create bean error!  PreDestory=" + getPreDestory());
			}
		return (T) instance;
	}

	// =======private static methods begin========
	private static boolean isEmptyStr(String str) {
		return (str == null || "".equals(str));
	}

	private static Class<?> ifExistBeanBoxClass(String className) {
		Class<?> newClass = null;
		try {
			newClass = Class.forName(className);
			if (BeanBox.class.isAssignableFrom((Class<?>) newClass))
				return newClass;
		} catch (Throwable e) {
		}
		return null;
	}

	private static BeanBox getBoxInstance(Class<?> clazz, BeanBoxContext context) {
		if (BeanBox.class.isAssignableFrom(clazz))
			return createBeanOrBoxInstance(clazz, context);
		else {
			String className = clazz.getName() + context.boxIdentity;
			Class<?> newClass = null;
			try {
				newClass = Class.forName(className);
			} catch (Throwable e) {
				className = clazz.getName() + "$" + clazz.getSimpleName() + context.boxIdentity;
				try {
					newClass = Class.forName(className);
				} catch (Throwable ee) {
					return new BeanBox(clazz, context);
				}
			}
			if (BeanBox.class.isAssignableFrom(newClass)) {
				BeanBox box = createBeanOrBoxInstance(newClass, context);
				if (box.getClassOrValue() == null)
					box.setClassOrValue(clazz);
				return box;
			} else
				printAndThrowError(null, "BeanBox getBox error! class named with identity \"" + context.boxIdentity
						+ "\" but is not a BeanBox class, class=" + className);
		}
		printAndThrowError(null, "BeanBox getBox error! clazz=" + clazz);
		return null;
	}

	// Create an instance by a class
	private static <T> T createBeanOrBoxInstance(Class<?> clazz, BeanBoxContext context) {
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
			printAndThrowError(null,
					"BeanBox createBeanOrBoxInstance error: no 0 parameter constructor found! boxClass=" + clazz);
		} catch (Exception e) {
			printAndThrowError(e, "BeanBox createBeanOrBoxInstance error! boxClass=" + clazz);
		}
		return null;
	}

	// ====================AOP about methods begin======================
	private static Object getProxyBean(Class<?> clazz, CopyOnWriteArrayList<Advisor> advisorList) {// use CGLib create
		// proxy bean, if
		// advice set for
		// this class
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new ProxyBean(clazz, advisorList));
		return enhancer.create();
	}

	private static void printAndThrowError(Exception e, String errorMsg) throws AssertionError {
		if (e != null)
			e.printStackTrace();
		throw new AssertionError(errorMsg);
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if necessary. The {@code setAccessible(true)}
	 * method is only called when actually necessary, to avoid unnecessary conflicts with a JVM SecurityManager (if
	 * active).
	 */
	private static void makeAccessible(Field field) {
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
	@SuppressWarnings("unused")
	private static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * Make the given constructor accessible, explicitly setting it accessible if necessary. The
	 * {@code setAccessible(true)} method is only called when actually necessary, to avoid unnecessary
	 */
	private static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
				&& !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	// If found advice for this class, use CGLib to create proxy bean, CGLIB is the only way to create proxy to make
	// source code simple.
	protected static boolean ifHaveAdvice(CopyOnWriteArrayList<Advisor> advisors, Object classOrValue) {
		if (classOrValue == null || !(classOrValue instanceof Class))
			return false;
		Method[] methods = ((Class<?>) classOrValue).getMethods();
		for (Method method : methods)
			for (Advisor adv : advisors)
				if (adv.match(((Class<?>) classOrValue).getName(), method.getName()))
					return true;
		return false;
	}

	// =========================Begin of BeanBoxContext==================
	public static class BeanBoxContext {
		private String boxIdentity = "Box";// any class with XxxBox will be looked as a beanBox class
		protected CopyOnWriteArrayList<Advisor> advisorList = new CopyOnWriteArrayList<Advisor>();// Advisors stored in
		// context
		protected HashMap<String, Object> signletonCache = new HashMap<String, Object>();// Singleton instance cached in
		// context
		protected CopyOnWriteArrayList<Class<?>> configClassList = new CopyOnWriteArrayList<Class<?>>();// Global
		// configurations
		protected ConcurrentHashMap<String, Method> preDestoryMethodCache = new ConcurrentHashMap<String, Method>();// preDestory

		// methods
		// cache
		public BeanBoxContext(Class<?>... configClasses) {
			for (Class<?> configClass : configClasses) {
				configClassList.add(configClass);
			}
		}

		public BeanBoxContext setBoxIdentity(String boxIdentity) {
			this.boxIdentity = boxIdentity;
			return this;
		}

		public String getBoxIdentity() {
			return boxIdentity;
		}

		public <T> T getBean(Class<?> clazz) {
			return BeanBox.getBoxInstance(clazz, this).getBean();
		}

		public BeanBoxContext addConfig(Class<?> configClass) {
			configClassList.add(configClass);
			return this;
		}

		public void close() {
			for (String beanID : preDestoryMethodCache.keySet()) {
				Object bean = signletonCache.get(beanID);
				Method method = preDestoryMethodCache.get(beanID);
				try {
					method.invoke(bean, new Object[] {});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			advisorList = new CopyOnWriteArrayList<Advisor>();
			signletonCache = new HashMap<String, Object>();
			configClassList = new CopyOnWriteArrayList<Class<?>>();
		}

		// ClassNameReg and methodNameReg use java Regex, Note: adviceAroundMethodName should be a public method
		public void setAOPAround(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(
					new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "AROUND", true));
		}

		// No explain, as method name said
		public void setAOPBefore(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(
					new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "BEFORE", true));
		}

		public void setAOPAfterReturning(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName,
					"AFTERRETURNING", true));
		}

		public void setAOPAfterThrowing(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName,
					"AFTERTHROWING", true));
		}

		public void setAspectjAround(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(
					new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "AROUND", false));
		}

		public void setAspectjBefore(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(
					new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "BEFORE", false));
		}

		public void setAspectjAfterReturning(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName,
					"AFTERRETURNING", false));
		}

		public void setAspectjAfterThrowing(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
				String adviceAroundMethodName) {
			advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName,
					"AFTERTHROWING", false));
		}
	}

	// =========================End of BeanBoxContext==================

	// =========== ProxyBean class begin ================
	private static class ProxyBean implements MethodInterceptor {// ProxyBean created by CGLib
		protected CopyOnWriteArrayList<Advisor> myAdvisors = new CopyOnWriteArrayList<Advisor>();

		protected ProxyBean(Class<?> clazz, CopyOnWriteArrayList<Advisor> globalAdvicors) {
			String beanClassName = clazz.getName();
			int i = beanClassName.indexOf("$$");// If created by CGLib, use the original class name as bean ID
			if (i > 0)
				beanClassName = beanClassName.substring(0, i);
			for (Advisor advisor : globalAdvicors) {// Make a copy from global advisors which only belong to this Bean
				Method[] methods = ((Class<?>) clazz).getMethods();
				for (Method method : methods)
					if (advisor.match(beanClassName, method.getName())) {
						myAdvisors.add(advisor);
						break;
					}
			}
		}

		public Object intercept(Object obj, Method method, Object[] args, MethodProxy cgLibMethodProxy)
				throws Throwable {
			if (myAdvisors.size() > 0 && myAdvisors.get(0).match(obj.getClass().getName(), method.getName()))
				return new AdviceCaller(this, obj, method, args, cgLibMethodProxy, myAdvisors).callNextAdvisor();// Start
			// a
			// advice
			// chain
			// call
			else
				return cgLibMethodProxy.invokeSuper(obj, args);
		}
	}

	// =========== ProxyBean class end ================

	// =============Advisor class begin===========
	private static class Advisor {// Advisor = Advice + Point-cut
		String classnameReg, methodNameReg, adviceMethodName;
		BeanBox adviceBeanBox;
		boolean isAOPAlliance = false;
		String adviceType;

		Advisor(String classnameReg, String methodNameReg, BeanBox adviceBeanBox, String adviceMethodName,
				String adviceType, boolean isAOPAlliance) {
			if (BeanBox.isEmptyStr(classnameReg) || BeanBox.isEmptyStr(methodNameReg) || adviceBeanBox == null
					|| BeanBox.isEmptyStr(adviceMethodName))
				throw new AssertionError(
						"BeanBox create Advisor error! ClassNameReg:" + classnameReg + " methodNameReg:" + methodNameReg
								+ " beanbox:" + adviceBeanBox + " aroundMethodName:" + adviceMethodName);
			this.classnameReg = classnameReg;
			this.methodNameReg = methodNameReg;
			this.adviceBeanBox = adviceBeanBox;
			this.adviceMethodName = adviceMethodName;
			this.isAOPAlliance = isAOPAlliance;
			this.adviceType = adviceType;
		}

		protected boolean match(String beanClassName, String methodName) {
			int i = beanClassName.indexOf("$$");
			if (i > 0)
				beanClassName = beanClassName.substring(0, i);
			return Pattern.compile(classnameReg).matcher(beanClassName).matches()
					&& Pattern.compile(methodNameReg).matcher(methodName).matches();
		}
	}

	// =============Advisor class end===========

	private static class AdviceCaller {
		protected final Object proxy;
		protected final Object target;
		protected final Method method;
		protected Object[] args;
		private MethodProxy cgLibMethodProxy;
		CopyOnWriteArrayList<Advisor> myAdvisors;
		protected int currentAdvisorIndex = -1;

		protected AdviceCaller(Object proxyBean, Object target, Method method, Object[] arguments,
				MethodProxy cgLibMethodProxy, CopyOnWriteArrayList<Advisor> myAdvisors) {
			this.proxy = proxyBean;
			this.target = target;
			this.method = method;
			this.args = arguments;
			this.myAdvisors = myAdvisors;
			this.cgLibMethodProxy = cgLibMethodProxy;
		}

		// Check and run the next advisor, first one no need check because already checked
		public Object callNextAdvisor() throws Throwable {
			if (this.currentAdvisorIndex >= this.myAdvisors.size() - 1)
				return cgLibMethodProxy.invokeSuper(target, args);
			Advisor advisor = myAdvisors.get(++this.currentAdvisorIndex);
			if (currentAdvisorIndex == 0 || advisor.match(target.getClass().getName(), method.getName())) {
				Object advice = advisor.adviceBeanBox.getBean();
				if (advisor.isAOPAlliance) {// AOP alliance type advice
					if ("AROUND".equals(advisor.adviceType)) {
						// public Object doAround(MethodInvocation caller) throws Throwable, AOP alliance & Spring's
						// around advice
						Method m = advice.getClass().getMethod(advisor.adviceMethodName,
								new Class[] { MethodInvocation.class });
						return m.invoke(advice, new AopAllianceInvocation(target, method, args, this));
					} else if ("BEFORE".equals(advisor.adviceType)) {
						// public void before(Method method, Object[] args, Object target) throws Throwable
						Method m = advice.getClass().getMethod(advisor.adviceMethodName,
								new Class[] { Method.class, Object[].class, Object.class });
						m.invoke(advice, new Object[] { method, args, target });
						return callNextAdvisor();
					} else if ("AFTERRETURNING".equals(advisor.adviceType)) {
						// public void afterReturning(Object result, Method method, Object[] args, Object target) throws
						// Throwable {}
						Object result = callNextAdvisor();
						Method m = advice.getClass().getMethod(advisor.adviceMethodName,
								new Class[] { Object.class, Method.class, Object[].class, Object.class });
						m.invoke(advice, new Object[] { result, method, args, target });
						return result;
					} else if ("AFTERTHROWING".equals(advisor.adviceType)) {
						// public void afterThrowing(Method method, Object[] args, Object target, Exception ex)
						// Detai see org.springframework.aop.ThrowsAdvice, here only implemented 4 arguments
						try {
							return callNextAdvisor();
						} catch (Exception ex) {
							Method m = advice.getClass().getMethod(advisor.adviceMethodName,
									new Class[] { Method.class, Object[].class, Object.class, Exception.class });
							m.invoke(advice, new Object[] { method, args, target, ex });
							throw ex;
						}
					}
				} else {// else is AspectJ advice, you can add your customized methods at here
					if ("AROUND".equals(advisor.adviceType)) {
						// public Object methodName(ProceedingJoinPoint caller) throws Throwable
						Method m = advice.getClass().getMethod(advisor.adviceMethodName,
								new Class[] { ProceedingJoinPoint.class });
						return m.invoke(advice, new AspectjProceedingJoinPoint(proxy, target, method, args, this));
					} else if ("BEFORE".equals(advisor.adviceType)) {
						// public void before(JoinPoint caller) throws Throwable
						Method m = advice.getClass().getMethod(advisor.adviceMethodName,
								new Class[] { JoinPoint.class });
						m.invoke(advice, new Object[] { new AspectjJoinPoint(proxy, target, method, args, this) });
						return callNextAdvisor();
					} else if ("AFTERRETURNING".equals(advisor.adviceType)) {
						// public void afterReturning(JoinPoint caller, Object result) throws Throwable
						Object result = callNextAdvisor();
						Method m = advice.getClass().getMethod(advisor.adviceMethodName,
								new Class[] { JoinPoint.class, Object.class });
						m.invoke(advice,
								new Object[] { new AspectjJoinPoint(proxy, target, method, args, this), result });
						return result;
					} else if ("AFTERTHROWING".equals(advisor.adviceType)) {
						// public void afterThrowing(JoinPoint caller, Exception ex) throws Throwable
						try {
							return callNextAdvisor();
						} catch (Exception ex) {
							Method m = advice.getClass().getMethod(advisor.adviceMethodName,
									new Class[] { JoinPoint.class, Exception.class });
							m.invoke(advice,
									new Object[] { new AspectjJoinPoint(proxy, target, method, args, this), ex });
							throw ex;
						}
					}
				}
				throw new AssertionError("BeanBox AdviceType not support error: " + advisor.adviceType);
			} else
				return callNextAdvisor();
		}
	}

	// ============AopAllianceInvocation class begin========
	private static class AopAllianceInvocation implements MethodInvocation {// MethodInvocation interface detail see
		// aopalliance doc
		private final Object target;
		private final Method method;
		private Object[] arguments;
		private final AdviceCaller caller;

		protected AopAllianceInvocation(Object target, Method method, Object[] arguments, AdviceCaller caller) {
			this.target = target;
			this.method = method;
			this.arguments = arguments;
			this.caller = caller;
		}

		public final Object getThis() {
			return this.target;
		}

		public final AccessibleObject getStaticPart() {
			return this.method;
		}

		public final Method getMethod() {
			return this.method;
		}

		public final Object[] getArguments() {
			return (this.arguments != null ? this.arguments : new Object[0]);
		}

		public Object proceed() throws Throwable {
			return caller.callNextAdvisor();
		}
	}

	// ============AopAllianceInvocation class end========

	private static class AspectjJoinPoint implements JoinPoint {// JoinPoint interface detail see Aspectj doc
		protected Object proxy;
		protected Object target;
		protected Method method;
		protected Signature signature;
		protected Object[] arguments;
		protected AdviceCaller caller;

		protected AspectjJoinPoint(Object proxyBean, Object target, Method method, Object[] arguments,
				AdviceCaller caller) {
			this.proxy = proxyBean;
			this.target = target;
			this.method = method;
			this.arguments = arguments;
			this.caller = caller;
		}

		public Object[] getArgs() {
			return arguments;
		}

		public String getKind() {
			return ProceedingJoinPoint.METHOD_EXECUTION;
		}

		public Signature getSignature() {
			if (this.signature == null)
				this.signature = new MethodSignatureImpl();
			return signature;
		}

		public SourceLocation getSourceLocation() {
			throw new UnsupportedOperationException();
		}

		public StaticPart getStaticPart() {
			throw new UnsupportedOperationException();
		}

		public Object getTarget() {
			return target;
		}

		public Object getThis() {
			return proxy;
		}

		public String toLongString() {
			return this.getClass().getName();
		}

		public String toShortString() {
			return this.getClass().getName();
		}

		protected class MethodSignatureImpl implements MethodSignature {// MethodSignature is required by Aspectj

			public String getName() {
				return method.getName();
			}

			public int getModifiers() {
				return method.getModifiers();
			}

			public Class<?> getDeclaringType() {
				return method.getDeclaringClass();
			}

			public String getDeclaringTypeName() {
				return method.getDeclaringClass().getName();
			}

			public Class<?> getReturnType() {
				return method.getReturnType();
			}

			public Method getMethod() {
				return method;
			}

			public Class<?>[] getParameterTypes() {
				return method.getParameterTypes();
			}

			public String[] getParameterNames() {// just throw unsupported exception
				throw new UnsupportedOperationException();
			}

			public Class<?>[] getExceptionTypes() {
				return method.getExceptionTypes();
			}

			public String toShortString() {
				return method.getName();
			}

			public String toLongString() {
				return method.getName();
			}
		}
	}

	// ============AspectjInvocation class begin========
	private static class AspectjProceedingJoinPoint extends AspectjJoinPoint implements ProceedingJoinPoint {// Detail
		// see
		// Aspectj
		// doc
		protected AspectjProceedingJoinPoint(Object proxyBean, Object target, Method method, Object[] arguments,
				AdviceCaller caller) {
			super(proxyBean, target, method, arguments, caller);
		}

		public Object proceed() throws Throwable {
			return caller.callNextAdvisor();
		}

		public Object proceed(Object[] args) throws Throwable {
			return proceed();
		}

		public void set$AroundClosure(AroundClosure arc) {
			throw new UnsupportedOperationException();
		}
	}

	// ============AspectjInvocation class end========

	// ==== InjectBox Annotation begin=====

	/**
	 * Inject BeanBox to a field with @Inject annotation <br/>
	 * 1) A.class{ @InjectBox(B.class) C c; ...} will find BeanBox class B and inject into c, if B is not beanBox, will
	 * find static class "B$CBox.class" inside of B <br/>
	 * 2) A.class{ @InjectBox C c; ...} will BeanBox follow this order: CBox.class in same package of C, "A$CBox.class",
	 * "ABox$CBox.class", CBox in globalConfigs
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface InjectBox {
		public Class<?> value() default Object.class;

		public boolean required() default true;// if set to false and no BeanBox found, do nothing. Otherwise throw a
		// Error
	}
	// ==== InjectBox Annotation end=====

}
// ===================================end of BeanBox===================
