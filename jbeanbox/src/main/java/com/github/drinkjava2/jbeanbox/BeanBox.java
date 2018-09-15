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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.Interceptor;

/**
 * BeanBox is a virtual model tell system how to build or lookup bean instance
 *
 * @author Yong Zhu
 * @since 2.4.7
 *
 */
public class BeanBox {

	// below fields for BeanBox has a target
	protected Object target; // inject can be constant, beanBox, beanBox class, class

	protected boolean constant = false; // mark the target is a constant

	protected Class<?> type; // For field and parameter constant inject, need know what's the type

	protected boolean required = true;// For field and parameter, if not found throw exception

	// below fields for BeanBox has no target
	protected Class<?> beanClass; // bean class, usually is an annotated class

	protected Boolean singleton; // default BeanBox is singleton

	protected List<Object> classInterceptors;// if not null, need create proxy

	protected Map<Method, List<Object>> methodInterceptors;// if not null, need create proxy

	protected Constructor<?> constructor; // if not null, use constructor to create

	protected BeanBox[] constructorParams; // store constructor parameters if have

	// ======================fill or call back stage====================

	protected Method postConstruct; // if not null, call these methods after built

	protected Method preDestroy; // if not null, call postConstruct after built

	protected Map<Field, BeanBox> fieldInjects;// if not null, inject Fields

	protected Map<Method, BeanBox[]> methodInjects;// if not null, inject Methods

	protected Method createMethod; // if not null, use this method to create bean

	protected Method configMethod; // if not null, after bean created, will call this method

	// ========== AOP About ===========
	protected Map<Method, List<Interceptor>> interceptors;

	public BeanBox() { // Default constructor
	}

	public Object getSingletonId() {
		if (singleton == null || !singleton || constant || target != null)
			return null;
		return this;
	}

	/** Use default global BeanBoxContext to create bean */
	public <T> T getBean() {
		return BeanBoxContext.globalBeanBoxContext.getBean(this);
	}

	/** Use default global BeanBoxContext to create bean */
	public static <T> T getBean(Object target) {
		return BeanBoxContext.globalBeanBoxContext.getBean(target);
	}

	/** Use given BeanBoxContext to create bean */
	public <T> T getBean(BeanBoxContext ctx) {
		return ctx.getBean(this);
	}

	public BeanBox newCopy() {
		BeanBox box = new BeanBox();
		box.target = this.target;
		box.beanClass = this.beanClass;
		box.constant = this.constant;
		box.singleton = this.singleton;
		box.required = this.required;
		box.classInterceptors = this.classInterceptors;
		box.methodInterceptors = this.methodInterceptors;
		box.constructor = this.constructor;
		box.constructorParams = this.constructorParams;
		box.fieldInjects = this.fieldInjects;
		box.methodInjects = this.methodInjects;
		box.postConstruct = this.postConstruct;
		box.preDestroy = this.preDestroy;
		box.createMethod = this.createMethod;
		box.configMethod = this.configMethod;
		return box;
	}

	protected String getDebugInfo() {
		StringBuilder sb = new StringBuilder("\r\n========BeanBox Debug for " + this + "===========\r\n");
		sb.append("target=" + this.target).append("\r\n");
		sb.append("constant=" + this.constant).append("\r\n");
		sb.append("type=" + this.type).append("\r\n");
		sb.append("required=" + this.required).append("\r\n");
		sb.append("beanClass=" + this.beanClass).append("\r\n");
		sb.append("singleton=" + this.singleton).append("\r\n");
		sb.append("classInterceptors=" + this.classInterceptors).append("\r\n");
		sb.append("methodInterceptors=" + this.methodInterceptors).append("\r\n");
		sb.append("constructor=" + this.constructor).append("\r\n");
		sb.append("constructorParams=" + this.constructorParams).append("\r\n");
		sb.append("postConstructs=" + this.postConstruct).append("\r\n");
		sb.append("preDestorys=" + this.preDestroy).append("\r\n");
		sb.append("fieldInjects=" + this.fieldInjects).append("\r\n");
		sb.append("methodInjects=" + this.methodInjects).append("\r\n");
		sb.append("createMethod=" + this.createMethod).append("\r\n");
		sb.append("configMethod=" + this.configMethod).append("\r\n");
		sb.append("========BeanBox Debug Info End===========");
		return sb.toString();
	}

	protected void checkOrCreateFieldInjects() {
		if (fieldInjects == null)
			fieldInjects = new HashMap<Field, BeanBox>();
	}

	protected void checkOrCreateMethodInjects() {
		if (methodInjects == null)
			methodInjects = new HashMap<Method, BeanBox[]>();
	}

	protected void checkOrCreateMethodInterceptors() {
		if (methodInterceptors == null)
			methodInterceptors = new HashMap<Method, List<Object>>();
	}

	protected void checkOrCreateClassInterceptors() {
		if (classInterceptors == null)
			classInterceptors = new ArrayList<Object>();
	}

	protected void belowAreJavaConfigMethods_______________() {// NOSONAR
	}

	public BeanBox setAsConstant(Object value) {
		this.constant = true;
		this.target = value;
		return this;
	}

	/**
	 * This method will be deprecated, use setSingleton() method as replace
	 */
	public BeanBox setPrototype(boolean isPrototype) {
		this.singleton = !isPrototype;
		return this;
	}

	public BeanBox injectConstruct(Class<?> clazz, Object... configs) {
		if (configs.length == 0) {
			this.constructor = BeanBoxUtils.getConstructor(clazz);
		} else {
			Class<?>[] paramTypes = new Class<?>[configs.length / 2];
			BeanBox[] params = new BeanBox[configs.length / 2];
			int mid = configs.length / 2;
			for (int i = 0; i < mid; i++)
				paramTypes[i] = (Class<?>) configs[i];
			for (int i = mid; i < configs.length; i++) {
				params[i - mid] = (BeanBox) configs[i];
				params[i - mid].setType(paramTypes[i - mid]);
			}
			this.constructor = BeanBoxUtils.getConstructor(clazz, paramTypes);
			this.constructorParams = params;
		}
		return this;
	}

	public BeanBox injectMethod(String methodName, Object... configs) {
		checkOrCreateMethodInjects();
		Class<?>[] paramTypes = new Class<?>[configs.length / 2];
		BeanBox[] params = new BeanBox[configs.length / 2];
		int mid = configs.length / 2;
		for (int i = 0; i < mid; i++)
			paramTypes[i] = (Class<?>) configs[i];
		for (int i = mid; i < configs.length; i++) {
			params[i - mid] = (BeanBox) configs[i];
			params[i - mid].setType(paramTypes[i - mid]);
		}
		Method m = ReflectionUtils.findMethod(beanClass, methodName, paramTypes);
		if (m != null)
			ReflectionUtils.makeAccessible(m);
		this.getMethodInjects().put(m, params);
		return this;
	}

	public BeanBox setPostConstruct(String methodName) {// NOSONAR
		Method m = ReflectionUtils.findMethod(beanClass, methodName);
		this.setPostConstruct(m);
		return this;
	}

	private static boolean ifWarnedSetPrototypePreDestroy = false;

	public BeanBox setPreDestroy(String methodName) {// NOSONAR
		if (!isSingleton() && !ifWarnedSetPrototypePreDestroy) {
			System.err.println("Warning: try to set a PreDestroy method '" + methodName// NOSONAR
					+ "' for a prototype bean, suggest set bean to singleton first.");
			BeanBoxException.throwEX("aa");
		}
		Method m = ReflectionUtils.findMethod(beanClass, methodName);
		this.setPreDestroy(m);
		return this;
	}

	public BeanBox injectField(String fieldName, BeanBox inject) {
		checkOrCreateFieldInjects();
		Field f = ReflectionUtils.findField(beanClass, fieldName);
		inject.setType(f.getType());
		ReflectionUtils.makeAccessible(f);
		this.getFieldInjects().put(f, inject);
		return this;
	}

	public BeanBox injectField(String fieldName, Object constValue) {
		checkOrCreateFieldInjects();
		Field f = ReflectionUtils.findField(beanClass, fieldName);
		BeanBox inject = new BeanBox();
		inject.setTarget(constValue);
		inject.setType(f.getType());
		inject.setConstant(true);
		ReflectionUtils.makeAccessible(f);
		this.getFieldInjects().put(f, inject);
		return this;
	}

	public boolean isSingleton() {
		return singleton != null && singleton;
	}

	protected void getterAndSetters_____________________() {// NOSONAR
	}

	public Object getTarget() {
		return target;
	}

	public BeanBox setTarget(Object target) {
		this.target = target;
		return this;
	}

	public boolean isConstant() {
		return constant;
	}

	public BeanBox setConstant(boolean constant) {
		this.constant = constant;
		return this;
	}

	public Class<?> getType() {
		return type;
	}

	public BeanBox setType(Class<?> type) {
		this.type = type;
		return this;
	}

	public boolean isRequired() {
		return required;
	}

	public BeanBox setRequired(boolean required) {
		this.required = required;
		return this;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public BeanBox setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
		return this;
	}

	public Boolean getSingleton() {
		return singleton;
	}

	public BeanBox setSingleton(Boolean singleton) {
		this.singleton = singleton;
		return this;
	}

	public List<Object> getClassInterceptors() {
		return classInterceptors;
	}

	public BeanBox setClassInterceptors(List<Object> classInterceptors) {
		this.classInterceptors = classInterceptors;
		return this;
	}

	public Map<Method, List<Object>> getMethodInterceptors() {
		return methodInterceptors;
	}

	public BeanBox setMethodInterceptors(Map<Method, List<Object>> methodInterceptors) {
		this.methodInterceptors = methodInterceptors;
		return this;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public BeanBox setConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
		return this;
	}

	public BeanBox[] getConstructorParams() {
		return constructorParams;
	}

	public BeanBox setConstructorParams(BeanBox[] constructorParams) {
		this.constructorParams = constructorParams;
		return this;
	}

	public Method getPostConstruct() {
		return postConstruct;
	}

	public BeanBox setPostConstruct(Method postConstruct) {
		this.postConstruct = postConstruct;
		return this;
	}

	public Method getPreDestroy() {
		return preDestroy;
	}

	public BeanBox setPreDestroy(Method preDestroy) {
		this.preDestroy = preDestroy;
		return this;
	}

	public Map<Field, BeanBox> getFieldInjects() {
		return fieldInjects;
	}

	public BeanBox setFieldInjects(Map<Field, BeanBox> fieldInjects) {
		this.fieldInjects = fieldInjects;
		return this;
	}

	public Map<Method, BeanBox[]> getMethodInjects() {
		return methodInjects;
	}

	public BeanBox setMethodInjects(Map<Method, BeanBox[]> methodInjects) {
		this.methodInjects = methodInjects;
		return this;
	}

	public Method getCreateMethod() {
		return createMethod;
	}

	public BeanBox setCreateMethod(Method createMethod) {
		this.createMethod = createMethod;
		return this;
	}

	public Method getConfigMethod() {
		return configMethod;
	}

	public BeanBox setConfigMethod(Method configMethod) {
		this.configMethod = configMethod;
		return this;
	}

	public Map<Method, List<Interceptor>> getInterceptors() {
		return interceptors;
	}

	public BeanBox setInterceptors(Map<Method, List<Interceptor>> interceptors) {
		this.interceptors = interceptors;
		return this;
	}

}
