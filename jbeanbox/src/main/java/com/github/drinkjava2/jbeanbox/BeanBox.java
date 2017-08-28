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

/**
 * For developers: this project is set to each line 120 characters.
 * 
 * Compare to last version 2.3, this version 2.4 fixed some bugs and put some
 * new functions like Java configuration and allow use @injectBox annotation on
 * constructor to allow build an object tree with out use configuration class
 */

package com.github.drinkjava2.jbeanbox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.drinkjava2.cglib3_2_0.proxy.Enhancer;
import com.github.drinkjava2.jbeanbox.BeanBoxUtils.ObjectType;
import com.github.drinkjava2.jbeanbox.springsrc.ReflectionUtils;

/**
 * jBeanBox is a macro scale IOC & AOP framework for Java 7 and above.
 * 
 * In version2.4.2 make make below changes:<br/>
 * 1)Fixed the bug can not find Parent class's method, this cause
 * setPostConstructor() and setPreDestory() doesn't work on child class <br/>
 * 2)Use logger to replace System.out.print <br/>
 * 3)Do some clean up within SONAR source code check<br/>
 * 4)Delete jbeanBox-samples Maven module, move unit test inside of jbeanbox
 * module<br/>
 * 5)Will direct use Spring's ReflectionUtils in this project, current
 * ReflectionUtils has some problems<br/>
 * 6)Allow add injectBox Annotation on method to indicate a AOP point-cut.
 * 
 * @author Yong Zhu (Yong9981@gmail.com)
 * @version 2.4.2-SNAPSHOT
 * @since 1.0
 * @update 2017-01-05
 * 
 */
@SuppressWarnings("unchecked")
public class BeanBox {
	private static final BeanBoxLogger log = BeanBoxLogger.getLog(BeanBox.class);

	private enum PropertyType {
		BEAN, VALUE, STATIC_FACTORY, BEAN_FACTORY
	}

	private static final String CREATE_BEAN = "create"; // Used to create bean instance manually(Java type safe)
	private static final String CONFIG_BEAN = "config"; // Used to set instance properties(Java type safe)

	private Object classOrValue;// Class or an Object
	private boolean isValueType = false; // if true means it's a value type, no need create instance
	private boolean prototype = false;// Default is singleTon, set prototype=true will each time return a new instance
	private Object[] constructorArgs;// for constructor injection use
	private Class<?>[] constructorTypes;// from 2.4.5 added, give constructor types
	private String postConstructor;
	private String preDestory;
	private ConcurrentHashMap<String, Object[]> properties = new ConcurrentHashMap<String, Object[]>();// properties
	public static final BeanBoxContext defaultContext = new BeanBoxContext();// this is a global default context
	private BeanBoxContext context = defaultContext;

	// Use a thread local counter to check circular dependency
	private static final ThreadLocal<Integer> circularCounter = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};

	/**
	 * Create a BeanBox
	 */
	public BeanBox() {
		// Default Constructor
	}

	/**
	 * Create a BeanBox and set classOrValue
	 */
	public BeanBox(Object classOrValue) {
		setClassOrValue(classOrValue);
	}

	/**
	 * Create a BeanBox and set constructorClass and constructorParameters
	 */
	public BeanBox(Class<?> constructorClass, Object... constructorParameters) {
		classOrValue = constructorClass;
		constructorArgs = constructorParameters;
	}

	/**
	 * Set constructorClass and constructorParameters for a BeanBox instance
	 */
	public BeanBox setConstructor(Class<?> constructorClass, Object... constructorParameters) {
		classOrValue = constructorClass;
		constructorArgs = constructorParameters;
		return this;
	}

	/**
	 * Set constructor parameter types, this method is useful when sometimes if only
	 * give constructorParameters but not enough to determine use which constructor
	 */
	public BeanBox setConstructorTypes(Class<?>... constructorTypes) {
		this.constructorTypes = constructorTypes;
		return this;
	}

	/**
	 * Set classOrValue for a BeanBox instance
	 */
	public BeanBox setClassOrValue(Object classOrValue) {
		this.classOrValue = classOrValue;
		if (!(classOrValue instanceof Class))
			isValueType = true;
		return this;
	}

	/**
	 * Set isValueType, if set true will not create class instance when getBean()
	 * method be called
	 */
	public BeanBox setValueType(boolean isValueType) {
		this.isValueType = isValueType;
		return this;
	}

	public Object getProperty(String property) {
		return (properties.get(property))[1];
	}

	public BeanBoxContext getContext() {
		return context;
	}

	public BeanBox setContext(BeanBoxContext context) {
		this.context = context;
		return this;
	}

	public boolean isPrototype() {
		return prototype;
	}

	/**
	 * Default is singleTon, if set true, each time will create a new instance when
	 * getBean() method be called
	 */
	public BeanBox setPrototype(boolean prototype) {
		this.prototype = prototype;
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

	/**
	 * Set postConstructor method, will be called after bean instance be created
	 */
	public BeanBox setPostConstructor(String postConstructor) {
		this.postConstructor = postConstructor;
		return this;
	}

	public String getPreDestory() {
		return preDestory;
	}

	/**
	 * Set preDestory method, will be called before BeanBox context be closed
	 */
	public BeanBox setPreDestory(String preDestory) {
		this.preDestory = preDestory;
		return this;
	}

	/**
	 * Set property, can be BeanBox class or normal class or value, for normal class
	 * will be wrapped as a BeanBox
	 */
	public BeanBox setProperty(String property, Object classOrValue) {
		ObjectType type = BeanBoxUtils.judgeType(classOrValue);
		switch (type) {
		case BEANBOX_INSTANCE:
			properties.put(property, new Object[] { PropertyType.BEAN, classOrValue });
			break;
		case BEANBOX_CLASS:// NOSONAR
			try {
				properties.put(property, new Object[] { PropertyType.BEAN,
						BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) classOrValue, context) });
			} catch (Exception e) {
				BeanBoxException.throwEX(e,
						"BeanBox setProperty error! property=" + property + " classOrValue=" + classOrValue);
			}
			break;
		case CLASS:
			properties.put(property, new Object[] { PropertyType.BEAN, new BeanBox(classOrValue) });
			break;
		case INSTANCE:
			properties.put(property, new Object[] { PropertyType.VALUE, classOrValue });
			break;
		default:
			BeanBoxException.throwEX(null, "BeanBox setProperty default case error");
		}
		return this;
	}

	/**
	 * Set static factory method and parameters
	 */
	public BeanBox setStaticFactory(String property, Class<?> staticFactoryClass, String methodName, Object... args) {
		properties.put(property, new Object[] { PropertyType.STATIC_FACTORY, staticFactoryClass, methodName, args });
		return this;
	}

	/**
	 * Set Bean factory method and parameters
	 */
	public BeanBox setBeanFactory(String property, BeanBox beanBox, String methodName, Object... args) {
		properties.put(property, new Object[] { PropertyType.BEAN_FACTORY, beanBox, methodName, args });
		return this;
	}

	/**
	 * Use CGLib create proxy bean, if advice set for this class
	 */
	public Object getProxyBean(Class<?> clazz, List<Advisor> advisorList) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new ProxyBean(clazz, advisorList, context));
		if (constructorArgs != null) {
			Class<?>[] argsTypes = constructorTypes;
			if (constructorTypes == null)
				argsTypes = guessObjectClassType(constructorArgs);
			return enhancer.create(argsTypes, constructorArgs);
		} else
			return enhancer.create();
	}

	/**
	 * Inject values into bean, use standard JDK reflection, bean setter methods are
	 * necessary
	 */
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
				Method m = ReflectionUtils.findMethod(c, (String) args[2], guessObjectClassType(beanArgs));
				Object beaninstance = m.invoke(c, BeanBoxUtils.getObjectRealValue(context, beanArgs));
				method.invoke(bean, new Object[] { beaninstance });
			} else if (((PropertyType) args[0]) == PropertyType.BEAN_FACTORY) {
				// PropertyType.BEAN_FACTORY, beanBox, methodName, args
				BeanBox bx = (BeanBox) args[1];
				bx.setContext(context);
				Object instance = bx.getBean();
				Object[] beanArgs = (Object[]) args[3];
				Method m = ReflectionUtils.findMethod(instance.getClass(), (String) args[2],
						guessObjectClassType(beanArgs));
				Object beaninstance = m.invoke(instance, BeanBoxUtils.getObjectRealValue(context, beanArgs));
				method.invoke(bean, new Object[] { beaninstance });
			}
		} catch (Exception e) {
			BeanBoxException.throwEX(e,
					"BeanBox invokeMethodToSetValue error! bean=" + bean + " method=" + method + " args=" + args);
		}
	}

	/**
	 * Inject values into bean, use field.set method, not recommend because it can
	 * inject into private field
	 */
	private void forceInjectFieldValue(Object bean, Field field, Object... args) {
		try {
			ReflectionUtils.makeAccessible(field);
			if (((PropertyType) args[0]) == PropertyType.VALUE) {
				field.set(bean, args[1]);
			} else if (((PropertyType) args[0]) == PropertyType.BEAN)
				field.set(bean, ((BeanBox) args[1]).setContext(context).getBean());
			else if (((PropertyType) args[0]) == PropertyType.STATIC_FACTORY) {
				Class<?> c = (Class<?>) args[1];
				Object[] beanArgs = (Object[]) args[3];
				Method m = ReflectionUtils.findMethod(c, (String) args[2], guessObjectClassType(beanArgs));
				Object beaninstance = m.invoke(c, BeanBoxUtils.getObjectRealValue(context, beanArgs));
				field.set(bean, beaninstance);
			} else if (((PropertyType) args[0]) == PropertyType.BEAN_FACTORY) {
				Object instance = ((BeanBox) args[1]).setContext(context).getBean();
				Object[] beanArgs = (Object[]) args[3];
				Method m = ReflectionUtils.findMethod(instance.getClass(), (String) args[2],
						guessObjectClassType(beanArgs));
				Object beaninstance = m.invoke(instance, BeanBoxUtils.getObjectRealValue(context, beanArgs));
				field.set(bean, beaninstance);
			}
		} catch (Exception e) {
			BeanBoxException.throwEX(e,
					"BeanBox invokeMethodToSetValue error! bean=" + bean + " field=" + field + " args=" + args);
		}
	}

	/**
	 * Inject properties values into bean instance
	 */
	private void injectInstancePropertyValues(Object instance) {
		Set<String> keys = properties.keySet();
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(instance.getClass());
		for (String property : keys) {
			boolean found = false;
			for (Method method : methods) {
				String setter = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
				if (method.getName().equals(setter)) {
					ReflectionUtils.makeAccessible(method);
					invokeMethodToSetValue(instance, method, properties.get(property));
					found = true;
				}
			}
			if (!found) {
				try {
					Field field = instance.getClass().getDeclaredField(property);
					forceInjectFieldValue(instance, field, properties.get(property));
				} catch (Exception e) {
					BeanBoxException.throwEX(e, "BeanBox setInstancePropertyValues error! class="
							+ this.getClassOrValue() + ", property ='" + property
							+ "', this may caused by inject value into Proxy bean, it's not supported by CGLib");
				}
			}
		}
	}

	/**
	 * For a field with @Inject annotation, find BeanBox or value, then inject into
	 * field
	 */
	private void injectAnnotationFields(Class<?> beanClass, Object beanInstance) {
		Field[] fields = beanClass.getDeclaredFields();
		for (Field field : fields) {
			InjectBox anno = field.getAnnotation(InjectBox.class);
			try {
				if (anno != null) {
					Object obj = BeanBoxUtils.getInjectFieldValue(beanClass, anno, field.getType(), field.getName(), 1,
							context);
					if (obj == null)// NOSONAR
						continue;
					obj = BeanBoxUtils.getRealValue(obj, context);
					if (obj != null) {// NOSONAR
						ReflectionUtils.makeAccessible(field);
						field.set(beanInstance, obj);
					}
				}
			} catch (Exception e) {
				BeanBoxException.throwEX(e,
						"BeanBox injectAnnotationFields error! beanClass=" + beanClass + " field=" + field.getName());
			}
		}
	}

	/**
	 * For a method with @Inject annotation, find BeanBox or value, then inject and
	 * call method
	 */
	private void injectAnnotationMethods(Class<?> beanClass, Object beanInstance) {
		Method[] methods = beanClass.getDeclaredMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(InjectBox.class)) {
				InjectBox a = m.getAnnotation(InjectBox.class);
				Class<?>[] parameterTypes = m.getParameterTypes();
				if (parameterTypes == null)
					return;
				int parameterCount = parameterTypes.length;
				if (parameterCount == 0 || parameterCount > 6)
					BeanBoxException.throwEX(null,
							"BeanBox buildBeanBoxWithAnotatedCtr error, only support at 1~6 method parameters,class="
									+ beanClass);
				Object[] args = new Object[parameterCount];
				for (int i = 0; i < parameterCount; i++)
					args[i] = BeanBoxUtils.getInjectFieldValue(beanClass, a, parameterTypes[i], null, i + 1, context);
				try {
					m.setAccessible(true);
					m.invoke(beanInstance, BeanBoxUtils.getObjectRealValue(context, args));
				} catch (Exception e) {
					BeanBoxException.throwEX(e,
							"BeanBox buildBeanBoxWithAnotatedCtr error,class=" + beanClass + " method=" + m.getName());
				}
			}
		}
	}

	/**
	 * Translate object[] to Class[], for invoke use
	 */
	private Class<?>[] guessObjectClassType(Object... beanArgs) {// Translate object[] to Class[], for invoke use
		Class<?>[] classes = new Class[beanArgs.length];
		for (int i = 0; i < classes.length; i++) {
			ObjectType type = BeanBoxUtils.judgeType(beanArgs[i]);
			switch (type) {
			case BEANBOX_INSTANCE: {// NOSONAR
				BeanBox b = (BeanBox) beanArgs[i];
				Method method = ReflectionUtils.findMethod(b.getClass(), CREATE_BEAN);
				if (method == null)
					classes[i] = (Class<?>) (b.getClassOrValue());
				else
					classes[i] = method.getReturnType();
			}
				break;
			case BEANBOX_CLASS:
				classes[i] = (Class<?>) BeanBoxUtils.createBeanBoxInstance((Class<BeanBox>) beanArgs[i], context)
						.getClassOrValue();
				break;
			case CLASS:
				classes[i] = (Class<?>) beanArgs[i];
				break;
			case INSTANCE:
				classes[i] = beanArgs[i].getClass();
				break;
			default:
				BeanBoxException.throwEX(null, "BeanBox getObjectClassType default case error");
			}
		}
		return classes;
	}

	/**
	 * Call config method in a BeanBox class, usually used to set bean instance
	 * properties
	 */
	private void callConfigBeanMethod(Object instance) throws AssertionError {
		Method setPropertiesMethod = null;
		try {
			Method[] methods = getClass().getDeclaredMethods();
			for (Method method : methods)
				if (CONFIG_BEAN.equals(method.getName())) {
					setPropertiesMethod = method;
					break;
				}
		} catch (Exception e) {
			BeanBoxException.eatException(e);
		}
		if (setPropertiesMethod != null) {
			ReflectionUtils.makeAccessible(setPropertiesMethod);
			try {
				setPropertiesMethod.invoke(this, new Object[] { instance });
			} catch (Exception e) {
				BeanBoxException.throwEX(e, "BeanBox  create bean error!  setPropertiesMethod=" + setPropertiesMethod);
			}
		}
	}

	private static Integer plusCircularCounter() {
		Integer check = circularCounter.get() + 1;
		circularCounter.set(check);
		return check;
	}

	private static void decreaseCircularCounter() {
		Integer check = circularCounter.get() - 1;
		circularCounter.set(check);
	}

	/**
	 * Create new bean instance or get singleTon bean instance in cache (if cached)
	 */
	public <T> T getBean() {// NOSONAR
		if (isValueType)
			return (T) classOrValue;
		Object instance = null;
		String beanBoxName = BeanBox.class.getName();
		String beanID = createBeanID(beanBoxName);

		if (plusCircularCounter() > 100) {// throw exception before out of stack memory
			decreaseCircularCounter();
			log.error("BeanBox getBean circular dependency error found! classOrValue=" + classOrValue);
			return null;
		}
		Method createBeanMethod = null;
		synchronized (context.signletonCache) {
			if (!prototype) {
				instance = context.signletonCache.get(beanID);
				if (instance != null) {
					decreaseCircularCounter();
					return (T) instance;// found singleTon bean in cache, good luck
				}
			}
			try {// Check if has create method in BeanBox
				createBeanMethod = ReflectionUtils.findMethod(getClass(), CREATE_BEAN);
				this.setClassOrValue(createBeanMethod.getReturnType());
			} catch (Exception e) {
				BeanBoxException.eatException(e);
			}
			if (createBeanMethod != null) {
				try {
					ReflectionUtils.makeAccessible(createBeanMethod);
					instance = createBeanMethod.invoke(this);//MayProblem
				} catch (Exception e) {
					decreaseCircularCounter();
					BeanBoxException.throwEX(e, "BeanBox getBean error! init method invoke error, class=" + this);
				}
			} else {
				if (BeanBoxUtils.ifHaveAdvice(context.advisorList, classOrValue)) {
					instance = getProxyBean((Class<?>) classOrValue, context.advisorList);
				} else if (constructorArgs != null) // first use given constructor to create instance
					try {
						instance = createBeanByGivenConstructor();
						if (instance == null)// NOSONAR
							BeanBoxException.throwEX(null,
									"BeanBox getBean error! not found given public constructor for class "
											+ classOrValue);
					} catch (Exception e) {
						decreaseCircularCounter();
						BeanBoxException.throwEX(e, "BeanBox create constructor error! constructor=" + classOrValue);
					}
				else if (classOrValue instanceof Class) {
					instance = BeanBoxUtils.createInstanceWithCtr0((Class<?>) classOrValue);
					if (instance == null) {
						if (!context.getIgnoreAnnotation()) // NOSONAR 3rd find annotated constructor
							instance = BeanBoxUtils.buildBeanBoxWithAnnotatedCtr((Class<?>) classOrValue, context);
						else {
							BeanBox bx = BeanBoxUtils.getBeanBox(null, (Class<?>) classOrValue, null, null, context,
									false);
							if (bx != null)// NOSONAR
								instance = bx.getBean();
						}
						if (instance == null) {// NOSONAR
							decreaseCircularCounter();
							BeanBoxException.throwEX(null, "BeanBox create bean error! class=" + classOrValue
									+ " no available constructor found.");
						}
					}
				} else {
					decreaseCircularCounter();
					BeanBoxException.throwEX(null, "BeanBox create bean undefined! classOrValue=" + classOrValue);
				}
			}

			if (!prototype) {
				context.signletonCache.put(beanID, instance);// save SingleTon in cache
				if (!BeanBoxUtils.isEmptyStr(this.getPreDestory())) {// save PreDestory methods in cache
					try {
						Method predestoryMethod = ReflectionUtils.findMethod(instance.getClass(), getPreDestory(), // NOSONAR
								new Class[] {});
						this.context.preDestoryMethodCache.put(beanID, predestoryMethod);
					} catch (Exception e) {
						decreaseCircularCounter();
						BeanBoxException.throwEX(e, "BeanBox  create bean error!  PreDestory=" + getPreDestory());
					}
				}
			}
		}
		if (instance == null) {
			decreaseCircularCounter();
			return null;
		}
		if (!context.getIgnoreAnnotation()) {
			injectAnnotationFields((Class<?>) classOrValue, instance);
			injectAnnotationMethods((Class<?>) classOrValue, instance);
		}
		callConfigBeanMethod(instance);// Check if have config method in BeanBox class
		injectInstancePropertyValues(instance);
		if (!BeanBoxUtils.isEmptyStr(getPostConstructor()))
			try {
				Method postConstr = ReflectionUtils.findMethod(instance.getClass(), getPostConstructor());//MayProblem
				postConstr.invoke(instance);//MayProblem
			} catch (Exception e) {
				decreaseCircularCounter();
				BeanBoxException.throwEX(e, "BeanBox create bean error! postConstructor=" + getPostConstructor());
			}
		decreaseCircularCounter();
		return (T) instance;
	}

	private String createBeanID(String beanBoxName) {
		String beanID = getClass().getName();
		if (beanID.equals(beanBoxName)) {
			if (this.getClassOrValue() instanceof Class)// use real bean class name & args as beanID
				beanID = ((Class<?>) this.getClassOrValue()).getName()
						+ (constructorArgs == null ? "" : constructorArgs);
			else
				BeanBoxException.throwEX(null, "BeanBox createOrGetFromCache error! BeanBox ID can not be determined!");
		}
		return beanID;
	}

	/**
	 * Create Bean instance by given constructor
	 */
	public Object createBeanByGivenConstructor() {
		Class<?>[] argsTypes = constructorTypes;
		if (constructorTypes == null)
			argsTypes = guessObjectClassType(constructorArgs);
		outer: for (Constructor<?> c : ((Class<?>) classOrValue).getConstructors()) {// NOSONAR
			Class<?>[] cType = c.getParameterTypes();
			if (cType.length != argsTypes.length)
				continue outer;
			for (int i = 0; i < cType.length; i++)
				if (!cType[i].isAssignableFrom(argsTypes[i]))
					continue outer;
			c.setAccessible(true);
			Object instance;
			try {
				instance = c.newInstance(BeanBoxUtils.getObjectRealValue(context, constructorArgs));
			} catch (Exception e) {
				return BeanBoxException.throwEX(e,
						"BeanBox createBeanByGivenConstructor error for constructorArgs: " + constructorArgs);
			}
			return instance;
		}
		return null;
	}

	/**
	 * Return a bean instance by class name, use default BeanBoxContext;
	 */
	public static <T> T getBean(Class<?> clazz) {
		return defaultContext.getBean(clazz);
	}

	/**
	 * Force return a prototype bean instance by class name
	 */
	public static <T> T getPrototypeBean(Class<?> clazz) {
		return defaultContext.getPrototypeBean(clazz);
	}

	/**
	 * Force return a singleton bean instance by class name
	 */
	public static <T> T getSingletonBean(Class<?> clazz) {
		return defaultContext.getSingletonBean(clazz);
	}

}
