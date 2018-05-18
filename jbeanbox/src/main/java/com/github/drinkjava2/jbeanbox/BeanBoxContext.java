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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BeanBoxContext acts the same role like ApplicationContext in Spring
 * 
 * @author Yong Zhu (Yong9981@gmail.com)
 * @since 1.0
 */
public class BeanBoxContext {

	/**
	 * The default BoxIdentity is "Box", BoxIdentity will be use for looking for
	 * BeanBox class
	 */
	String boxIdentity = "Box";
	private static final String BEAN_BOX_CLASS_NAME = BeanBox.class.getName();

	Boolean ignoreAnnotation = false; // if set true, will ignore @injectBox annotation

	/** Advisors cache */
	protected CopyOnWriteArrayList<Advisor> advisorList = new CopyOnWriteArrayList<Advisor>();

	/** Singleton instance cache */
	protected HashMap<String, Object> signletonCache = new HashMap<String, Object>();

	/** Configuration file class cache */
	private List<Class<?>> configClassList = new CopyOnWriteArrayList<Class<?>>();

	/** preDestory method cache */
	protected ConcurrentHashMap<String, Method> preDestoryMethodCache = new ConcurrentHashMap<String, Method>();

	/**
	 * AOP around annotations setting, setAopAroundValue() method can set a default
	 * value to an AopAround type annotation
	 */
	protected Map<Class<?>, Class<?>> aopAroundAnnotationsMap = new ConcurrentHashMap<Class<?>, Class<?>>();

	public BeanBoxContext(Class<?>... configClasses) {
		for (Class<?> configClass : configClasses) {
			configClassList.add(configClass);
		}
	}

	/**
	 * Set BoxIdentity, BoxIdentity will be use for looking for BeanBox class
	 */
	public BeanBoxContext setBoxIdentity(String boxIdentity) {
		this.boxIdentity = boxIdentity;
		return this;
	}

	/**
	 * Set a Box identity used to find configuration, default is "Box"
	 */
	public String getBoxIdentity() {
		return boxIdentity;
	}

	public List<Class<?>> getConfigClassList() {
		return configClassList;
	}

	/**
	 * Build or find a BeanBox and create a bean instance
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<?> clazz) {
		String beanID = clazz.getName();
		if (!BEAN_BOX_CLASS_NAME.equals(beanID) && signletonCache.containsKey(beanID))
			return (T) signletonCache.get(beanID);
		BeanBox box = BeanBoxUtils.getBeanBox(null, clazz, null, null, this, true);
		if (!box.isPrototype())
			synchronized (signletonCache) {
				Object obj = box.getBean();
				signletonCache.put(beanID, obj);
				return (T) obj;
			}
		else
			return box.getBean();
	}

	/**
	 * Build or find a BeanBox and force create a prototype Bean instance
	 */
	public <T> T getPrototypeBean(Class<?> clazz) {
		return BeanBoxUtils.getBeanBox(null, clazz, null, null, this, true).setPrototype(true).getBean();
	}

	/**
	 * Build or find a BeanBox and force create a singleton Bean instance
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSingletonBean(Class<?> clazz) {
		String beanID = clazz.getName();
		if (!BEAN_BOX_CLASS_NAME.equals(beanID) && signletonCache.containsKey(beanID))
			return (T) signletonCache.get(beanID);
		BeanBox box = BeanBoxUtils.getBeanBox(null, clazz, null, null, this, true).setPrototype(false);
		if (!box.isPrototype())
			synchronized (signletonCache) {
				Object obj = box.getBean();
				signletonCache.put(beanID, obj);
				return (T) obj;
			}
		else
			return box.getBean();
	}

	/**
	 * Register configuration classes
	 */
	public BeanBoxContext addConfig(Class<?> configClass) {
		configClassList.add(configClass);
		return this;
	}

	public Boolean getIgnoreAnnotation() {
		return ignoreAnnotation;
	}

	/**
	 * If set true, will ignore all annotations
	 */
	public BeanBoxContext setIgnoreAnnotation(Boolean ignoreAnnotation) {
		this.ignoreAnnotation = ignoreAnnotation;
		return this;
	}

	/**
	 * When close() method be called, call preDestory() methods for all cached
	 * singleTon Beans.
	 */
	public void close() {
		for (Entry<String, Method> entry : preDestoryMethodCache.entrySet()) {
			String beanID = entry.getKey();
			Object bean = signletonCache.get(beanID);
			Method method = entry.getValue();
			try {
				method.invoke(bean, new Object[] {});// NOSONAR
			} catch (Exception e) {
				throw new BeanBoxException("BeanBox Context closing exception found. ", e);
			}
		}
		boxIdentity = "Box";
		advisorList = new CopyOnWriteArrayList<Advisor>();
		signletonCache = new HashMap<String, Object>();
		configClassList = new CopyOnWriteArrayList<Class<?>>();
		preDestoryMethodCache = new ConcurrentHashMap<String, Method>();
	}

	/**
	 * Set AOPAround, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAOPAround(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList
				.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "AROUND", true));
	}

	/**
	 * Set AOPAround, ClassNameReg and methodNameReg use java Regex,
	 * adviceAroundMethodName is "invoke"
	 */
	public void setAOPAround(String classNameReg, String methodNameReg, BeanBox adviceBeanBox) {
		advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, "invoke", "AROUND", true));
	}

	/**
	 * Set AOPBefore, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAOPBefore(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList
				.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "BEFORE", true));
	}

	/**
	 * Set AOPAfterReturning, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAOPAfterReturning(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName,
				"AFTERRETURNING", true));
	}

	/**
	 * Set AOPAfterThrowing, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAOPAfterThrowing(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList.add(
				new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "AFTERTHROWING", true));
	}

	public List<Advisor> getAdvisorList() {
		return advisorList;
	}

	// getter & setter====
	/**
	 * Set Advisor List, usually no need use this method except experts
	 */
	public void setAdvisorList(CopyOnWriteArrayList<Advisor> advisorList) {// NOSONAR
		this.advisorList = advisorList;
	}

	/**
	 * Get singleton cache, usually no need use this method except experts
	 */
	public HashMap<String, Object> getSignletonCache() {// NOSONAR
		return signletonCache;
	}

	/**
	 * Set singleton cache, usually no need use this method except experts
	 */
	public void setSignletonCache(HashMap<String, Object> signletonCache) {// NOSONAR
		this.signletonCache = signletonCache;
	}

	/**
	 * Get PreDestory Method Cache, usually no need use this method except experts
	 */
	public ConcurrentHashMap<String, Method> getPreDestoryMethodCache() {// NOSONAR
		return preDestoryMethodCache;
	}

	/**
	 * Set PreDestory Method Cache, usually no need use this method except experts
	 */
	public void setPreDestoryMethodCache(ConcurrentHashMap<String, Method> preDestoryMethodCache) {// NOSONAR
		this.preDestoryMethodCache = preDestoryMethodCache;
	}

	/**
	 * Set config class list, usually no need use this method except experts
	 */
	public void setConfigClassList(List<Class<?>> configClassList) {
		this.configClassList = configClassList;
	}

	/** Return current Aop Around Annotations Map */
	public Map<Class<?>, Class<?>> getAopAroundAnnotationsMap() {
		return aopAroundAnnotationsMap;
	}

	/**
	 * Register an AOP Around annotation, usage:
	 * beanBoxContext.setAopAroundValue(Tx.class, FooBarBox.class);
	 */
	public void regAopAroundAnnotation(Class<?> annotationClass, Class<?> defaultBoxClass) {
		aopAroundAnnotationsMap.put(annotationClass, defaultBoxClass);
	}

	/**
	 * Register an AOP Around annotation, usage:
	 * beanBoxContext.setAopAroundValue(Tx.class, FooBarBox.class);
	 */
	public void regAopAroundAnnotation(Class<?> annotationClass) {
		aopAroundAnnotationsMap.put(annotationClass, Object.class);
	}
}