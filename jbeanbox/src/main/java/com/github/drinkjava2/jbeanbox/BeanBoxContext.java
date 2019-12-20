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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Qualifier;

import org.springframework.stereotype.Component;

import com.github.drinkjava2.jbeanbox.ValueTranslator.DefaultValueTranslator;
import com.github.drinkjava2.jbeanbox.annotation.COMPONENT;
import com.github.drinkjava2.jbeanbox.annotation.NAMED;
import com.github.drinkjava2.jbeanbox.annotation.QUALIFILER;

/**
 * BeanBoxContext is the Context to create beans
 * 
 * @author Yong Zhu
 * @since 2.4
 *
 */
public class BeanBoxContext {
	public static String CREATE_METHOD = "create"; // as title
	public static String CONFIG_METHOD = "config"; // as title

	protected static boolean globalNextAllowAnnotation = true; // as title
	protected static boolean globalNextAllowSpringJsrAnnotation = true; // as title
	protected static ValueTranslator globalNextValueTranslator = new DefaultValueTranslator(); // see user manual

	protected boolean allowAnnotation = globalNextAllowAnnotation;
	protected boolean allowSpringJsrAnnotation = globalNextAllowSpringJsrAnnotation;
	protected ValueTranslator valueTranslator = globalNextValueTranslator;

	protected Map<Object, Object> bindCache = new ConcurrentHashMap<Object, Object>();// bind cache
	protected Map<Class<?>, BeanBox> componentCache = new ConcurrentHashMap<Class<?>, BeanBox>(); // component Cache
	protected Map<Object, Object> singletonCache = new ConcurrentHashMap<Object, Object>(); // class or BeanBox as key

	protected static BeanBoxContext globalBeanContext = new BeanBoxContext();// Global Bean context

	// ==========AOP about=========
	protected List<Object[]> aopRules;

	public BeanBoxContext() {
		bind(Object.class, EMPTY.class);
		bind(String.class, EMPTY.class);
		bind(Integer.class, EMPTY.class);
		bind(Boolean.class, EMPTY.class);
		bind(Byte.class, EMPTY.class);
		bind(Long.class, EMPTY.class);
		bind(Short.class, EMPTY.class);
		bind(Float.class, EMPTY.class);
		bind(Double.class, EMPTY.class);
		bind(Character.class, EMPTY.class);
		bind(List.class, EMPTY.class);
		bind(Map.class, EMPTY.class);
		bind(Set.class, EMPTY.class);

		bind(int.class, EMPTY.class);
		bind(boolean.class, EMPTY.class);
		bind(byte.class, EMPTY.class);
		bind(long.class, EMPTY.class);
		bind(short.class, EMPTY.class);
		bind(float.class, EMPTY.class);
		bind(double.class, EMPTY.class);
		bind(char.class, EMPTY.class);
	}

	/**
	 * Reset global variants setting , note this method only close
	 * globalBeanContext, if created many BeanBoxContext instance need close them
	 * manually
	 */
	public static void reset() {
		globalBeanContext.close();
		globalNextAllowAnnotation = true;
		globalNextAllowSpringJsrAnnotation = true;
		globalNextValueTranslator = new DefaultValueTranslator();
		CREATE_METHOD = "create";
		CONFIG_METHOD = "config";
		globalBeanContext = new BeanBoxContext();
	}

	/**
	 * Close current BeanBoxContext, clear singlton cache, call predestory methods
	 * for each singleton if they have
	 */
	public void close() {
		for (Entry<Object, Object> singletons : singletonCache.entrySet()) {
			Object key = singletons.getKey();
			Object obj = singletons.getValue();
			if (key instanceof BeanBox) {
				BeanBox box = (BeanBox) key;
				if (box.getPreDestroy() != null)
					try {
						box.getPreDestroy().invoke(obj);
					} catch (Exception e) {
						// Eat it here, but usually need log it
					}
			}
		}
		bindCache.clear();
		componentCache.clear();
		singletonCache.clear();
	}

	public Object getObject(Object obj) {
		return getBean(obj, true, null);
	}

	public <T> T getBean(Object obj) {
		return getBean(obj, true, null); // first step of changzheng
	}

	public <T> T getInstance(Class<T> target) {
		return getBean(target, true, null);
	}

	public <T> T getBean(Object obj, boolean required) {
		return getBean(obj, required, null);
	}

	public <T> T getInstance(Class<T> target, boolean required) {
		return getBean(target, required, null);
	}

	@SuppressWarnings("unchecked")
	protected <T> T getBean(Object target, boolean required, Set<Object> history) {// NOSONAR
		// NOSONAR System.out.println(" target=" + target + " history=" + history);
		if (target != null && singletonCache.containsKey(target))
			return (T) singletonCache.get(target);

		if (target == null || EMPTY.class == target)
			return (T) notfoundOrException(target, required);

		if (target instanceof BeanBox) {
			BeanBox bx = (BeanBox) target;
			if (bx.isSingleton()) { // BeanBox already in singleton cache?
				Object id = bx.getSingletonId();
				if (id != null) {
					Object existed = singletonCache.get(id);
					if (existed != null && EMPTY.class != existed)
						return (T) existed;
				}
			}
			if (history != null && history.contains(target)) {
				if (bx.getTarget() != null)
					BeanBoxException
							.throwEX("Fail to build bean, circular dependency found on target: " + bx.getTarget());
				if (bx.getBeanClass() != null)
					BeanBoxException.throwEX(
							"Fail to build bean, circular dependency found on beanClass: " + bx.getBeanClass());
				if (bx.getType() != null)
					BeanBoxException.throwEX("Fail to build bean, circular dependency found on type: " + bx.getType());
				if (bx.getCreateMethod() != null)
					BeanBoxException.throwEX(
							"Fail to build bean, circular dependency found on method: " + bx.getCreateMethod());
				BeanBoxException.throwEX("Fail to build bean, circular dependency found on: " + bx);
			}
		}

		Object result = null;
		if (history == null)
			history = new HashSet<Object>();
		history.add(target);
		if (bindCache.containsKey(target)) {
			result = getBean(bindCache.get(target), required, history);
		} else if (target instanceof BeanBox) { // is a BeanBox instance?
			result = getBeanFromBox((BeanBox) target, required, history);
		} else if (target instanceof Class) { // is a class?
			BeanBox box = BeanBoxUtils.getUniqueBeanBox(this, (Class<?>) target);
			result = getBean(box, required, history);
			if (EMPTY.class != result && box.isSingleton()) {
				singletonCache.put(target, result);
			}
		} else
			result = notfoundOrException(target, required);
		history.remove(target);
		return (T) result;
	}

	/** Get Bean From BeanBox instance */
	private Object getBeanFromBox(BeanBox box, boolean required, Set<Object> history) {// NOSONAR
		// NOSONAR System.out.println(" Box=> box=" + box + " history=" + history);
		BeanBoxException.assureNotNull(box, "Fail to build instance for a null beanBox");
		Object bean = null;
		if (box.isSingleton()) { // Check if singleton in cache
			bean = singletonCache.get(box);
			if (bean != null)
				return bean;
		}

		if (box.isPureValue()) // if constant?
			return box.getTarget();
		if (box.getTarget() != null) {// if target?
			if (EMPTY.class != box.getTarget())
				return getBean(box.getTarget(), box.isRequired(), history);
			if (box.getType() != null)
				return getBean(box.getType(), box.isRequired(), history);
			else
				return notfoundOrException(box.getTarget(), box.isRequired());
		}

		boolean aopFound = false;// is AOP?
		if (box.getAopRules() != null || box.getMethodAops() != null)
			aopFound = true;
		else if (this.getAopRules() != null && box.getBeanClass() != null)
			for (Object[] aops : this.getAopRules()) // global AOP
				if (NameMatchUtil.nameMatch((String) aops[1], box.getBeanClass().getName())) {
					aopFound = true;
					break;
				}
		if (aopFound)
			bean = AopUtils.createProxyBean(box.getBeanClass(), box, this);
		else if (box.getCreateMethod() != null) // if have create method?
			try {
				Method m = box.getCreateMethod();
				if (m.getParameterTypes().length == 1) {
					bean = m.invoke(box, new Caller(this, required, history, null));
				} else if (m.getParameterTypes().length == 0)
					bean = m.invoke(box);
				else
					BeanBoxException.throwEX("Create method can only have 0 or 1 parameter");
				BeanBoxException.assureNotNull(bean, "Create method created a null object.");
			} catch (Exception e) {
				return BeanBoxException.throwEX(e);
			}
		else if (box.getConstructor() != null) { // has constructor?
			if (box.getConstructorParams() != null && box.getConstructorParams().length > 0) {
				Object[] initargs = param2RealObjects(this, history, box.getConstructorParams());
				try {
					bean = box.getConstructor().newInstance(initargs);
				} catch (Exception e) {
					return BeanBoxException.throwEX(e);
				}
			} else // 0 param constructor
				try {
					bean = box.getConstructor().newInstance();
				} catch (Exception e) {
					return BeanBoxException.throwEX(e);
				}
		} else if (box.getBeanClass() != null) { // is normal bean
			if (EMPTY.class == box.getBeanClass())
				return notfoundOrException(EMPTY.class, required);
			try {
				bean = box.getBeanClass().newInstance();
			} catch (Exception e) {
				BeanBoxException.throwEX("Failed to call 0 parameter constructor of: " + box.getBeanClass(), e);
			}
		} else
			return notfoundOrException(null, required); // return null or throw EX

		// Now Bean is ready

		// Cache bean or proxy bean right now for circular dependency use
		if (box.isSingleton()) {
			Object id = box.getSingletonId();
			if (id != null)
				singletonCache.put(box, bean);
		} // NOW BEAN IS CREATED

		if (box.getConfigMethod() != null) {// ====config method of this BeanBox
			try {
				Method m = box.getConfigMethod();
				if (m.getParameterTypes().length == 2)
					m.invoke(box, bean, new Caller(this, required, history, bean));
				else if (m.getParameterTypes().length == 1)
					m.invoke(box, bean);
				else
					BeanBoxException.throwEX("Config method can only have 1 or 2 parameters");
			} catch (Exception e) {
				return BeanBoxException.throwEX(e);
			}
		}

		if (box.getPostConstruct() != null) // PostConstructor
			ReflectionUtils.invokeMethod(box.getPostConstruct(), bean);

		if (box.getFieldInjects() != null) // Fields inject
			for (Entry<Field, BeanBox> entry : box.getFieldInjects().entrySet()) {
				Field f = entry.getKey();
				BeanBox b = entry.getValue();
				Object fieldValue = this.getBeanFromBox(b, false, history);
				if (EMPTY.class == fieldValue) {
					if (b.isRequired())
						BeanBoxException.throwEX("Not found required value for field: " + f.getName() + " in "
								+ f.getDeclaringClass().getName());
				} else {
					if (fieldValue != null && fieldValue instanceof String)
						fieldValue = this.valueTranslator.translate((String) fieldValue, b.getType());
					ReflectionUtils.setField(f, bean, fieldValue);
				}
			}

		if (box.getMethodInjects() != null) { // Methods inject
			for (Entry<Method, BeanBox[]> methods : box.getMethodInjects().entrySet()) {
				Method m = methods.getKey();
				BeanBox[] paramBoxs = methods.getValue();
				if (paramBoxs != null && paramBoxs.length > 0) {
					Object[] methodParams = param2RealObjects(this, history, paramBoxs);
					ReflectionUtils.invokeMethod(m, bean, methodParams);
				} else // method has no parameter
					ReflectionUtils.invokeMethod(m, bean);
			}
		}
		return bean;
	}

	/**
	 * Scan classes with &#064;COMPONENT or &#064;Component annotation, for autowire
	 * purpose, usually used for inject bean fields without need manually bind
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void scanComponents(String... packages) {
		List<Class> classes = ClassScanner.scan(packages);
		for (Class claz : classes)
			for (Annotation anno : claz.getAnnotations()) {
				Class<? extends Annotation> aType = anno.annotationType();
				if (BeanBoxUtils.ifSameOrChildAnno(aType, COMPONENT.class, Component.class)) {
					BeanBox box = getBeanBox(claz);
					componentCache.put(claz, box);
					Map<String, Object> values = BeanBoxUtils.changeAnnotationValuesToMap(anno);
					if (!"".equals(values.get("value")))// use given bean name
						this.bind(values.get("value"), claz);
					else {
						String s = claz.getSimpleName(); // else use first char lower case class name as bean name
						if (!Character.isLowerCase(s.charAt(0)))
							s = (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1))
									.toString();
						bind(s, claz);
					}

					for (Annotation otherAnno : claz.getAnnotations()) {// check qualifiler or named annotation family
						Class<? extends Annotation> anno2 = otherAnno.annotationType();
						if (BeanBoxUtils.ifSameOrChildAnno(anno2, NAMED.class, NAMED.class, QUALIFILER.class,
								Qualifier.class, org.springframework.beans.factory.annotation.Qualifier.class)) {
							Map<String, Object> v = BeanBoxUtils.changeAnnotationValuesToMap(anno);
							if (v.size() > 1)
								BeanBoxException.throwEX(
										"BeanBox does not support multiple property in Qualifier annotation " + anno2);
							String annoName = anno2.getSimpleName().toUpperCase();
							bind(annoName + ":" + (v.isEmpty() ? "" : v.values().iterator().next()), claz);
						}
					}
				}
			}
	}

	/** Bind a targe on a bean id, if id already exist, throw BeanBoxException */
	public BeanBoxContext bind(Object id, Object target) {
		BeanBoxException.assureNotNull(id, "bind id can not be empty");
		if (bindCache.containsKey(id))
			BeanBoxException.throwEX("Fail to bind duplicated bean id '" + id
					+ "', consider use 'rebind' method to allow override existed binding");
		bindCache.put(id, target);
		return this;
	}

	/** Bind a targe on a bean id, if id already exist, override it */
	public BeanBoxContext rebind(Object id, Object target) {
		BeanBoxException.assureNotNull(id, "bind id can not be empty");
		bindCache.put(id, target);
		return this;
	}

	public BeanBoxContext addContextAop(Object aop, String classNameRegex, String methodNameRegex) {
		if (aopRules == null)
			aopRules = new ArrayList<Object[]>();
		aopRules.add(new Object[] { BeanBoxUtils.checkAOP(aop), classNameRegex, methodNameRegex });
		return this;
	}

	public BeanBoxContext addContextAop(Object aop, Class<?> clazz, String methodNameRegex) {
		return addContextAop(aop, clazz.getName() + "*", methodNameRegex);
	}

	public BeanBox getBeanBox(Class<?> clazz) {
		return BeanBoxUtils.getUniqueBeanBox(this, clazz);
	}

	protected void staticMethods________________________() {// NOSONAR
	}

	private static Object[] param2RealObjects(BeanBoxContext ctx, Set<Object> history, BeanBox[] boxes) {
		Object[] result = new Object[boxes.length];
		for (int i = 0; i < boxes.length; i++) {
			result[i] = ctx.getBeanFromBox(boxes[i], true, history);
			if (result[i] != null && result[i] instanceof String)
				result[i] = ctx.valueTranslator.translate((String) result[i], boxes[i].getType());
		}
		return result;
	}

	private static Object notfoundOrException(Object target, boolean required) {
		if (required)
			return BeanBoxException.throwEX("BeanBox target not found: " + target);
		else
			return EMPTY.class;
	}

	protected void notExistMethod() {// this mark a not exist Method

	}

	protected static Method NOT_EXIST_METHOD = null; // NOSONAR

	static {
		try {
			NOT_EXIST_METHOD = BeanBoxContext.class.getDeclaredMethod("notExistMethod");
		} catch (Exception e) {// NOSONAR
		}
	}

	protected void staticGetterAndSetters________________________() {// NOSONAR
	}

	public static BeanBoxContext getGlobalBeanContext() {
		return globalBeanContext;
	}

	public static void setGlobalBeanContext(BeanBoxContext globalBeanContext) {
		BeanBoxContext.globalBeanContext = globalBeanContext;
	}

	public static boolean isGlobalNextAllowAnnotation() {
		return globalNextAllowAnnotation;
	}

	public static void setGlobalNextAllowAnnotation(boolean globalNextAllowAnnotation) {
		BeanBoxContext.globalNextAllowAnnotation = globalNextAllowAnnotation;
	}

	public static boolean isGlobalNextAllowSpringJsrAnnotation() {
		return globalNextAllowSpringJsrAnnotation;
	}

	public static void setGlobalNextAllowSpringJsrAnnotation(boolean globalNextAllowSpringJsrAnnotation) {
		BeanBoxContext.globalNextAllowSpringJsrAnnotation = globalNextAllowSpringJsrAnnotation;
	}

	public static ValueTranslator getGlobalNextParamTranslator() {
		return globalNextValueTranslator;
	}

	public static void setGlobalNextParamTranslator(ValueTranslator globalNextParamTranslator) {
		BeanBoxContext.globalNextValueTranslator = globalNextParamTranslator;
	}

	public static Method getNotExistMethod() {
		return NOT_EXIST_METHOD;
	}

	public static void setNotExistMethod(Method notExistMethod) {
		BeanBoxContext.NOT_EXIST_METHOD = notExistMethod;
	}

	protected void getterAndSetters________________________() {// NOSONAR
	}

	public boolean isAllowAnnotation() {
		return allowAnnotation;
	}

	public BeanBoxContext setAllowAnnotation(boolean allowAnnotation) {
		this.allowAnnotation = allowAnnotation;
		return this;
	}

	public boolean isAllowSpringJsrAnnotation() {
		return allowSpringJsrAnnotation;
	}

	public BeanBoxContext setAllowSpringJsrAnnotation(boolean allowSpringJsrAnnotation) {
		this.allowSpringJsrAnnotation = allowSpringJsrAnnotation;
		return this;
	}

	public ValueTranslator getValueTranslator() {
		return valueTranslator;
	}

	public BeanBoxContext setValueTranslator(ValueTranslator valueTranslator) {
		this.valueTranslator = valueTranslator;
		return this;
	}

	public Map<Object, Object> getBindCache() {
		return bindCache;
	}

	public BeanBoxContext setBindCache(Map<Object, Object> bindCache) {
		this.bindCache = bindCache;
		return this;
	}

	public Map<Class<?>, BeanBox> getComponentCache() {
		return componentCache;
	}

	public BeanBoxContext setComponentCache(Map<Class<?>, BeanBox> beanBoxMetaCache) {
		this.componentCache = beanBoxMetaCache;
		return this;
	}

	public Map<Object, Object> getSingletonCache() {
		return singletonCache;
	}

	public BeanBoxContext setSingletonCache(Map<Object, Object> singletonCache) {
		this.singletonCache = singletonCache;
		return this;
	}

	public List<Object[]> getAopRules() {
		return aopRules;
	}

	public BeanBoxContext setAopRules(List<Object[]> aopRules) {
		this.aopRules = aopRules;
		return this;
	}

}
