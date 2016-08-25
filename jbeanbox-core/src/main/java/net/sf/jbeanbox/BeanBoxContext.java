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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BeanBoxContext acts the same role like ApplicationContext in Spring
 * 
 * @author Yong Zhu
 * @since 2016-2-13
 * @update 2016-08-21
 *
 */
public class BeanBoxContext {
	// The default BoxIdentity is "Box", BoxIdentity will be use for looking for BeanBox class
	String boxIdentity = "Box";

	// Advisors cache
	protected CopyOnWriteArrayList<Advisor> advisorList = new CopyOnWriteArrayList<Advisor>();

	// Singleton instance cache
	protected HashMap<String, Object> signletonCache = new HashMap<String, Object>();

	// Configuration class cache
	protected CopyOnWriteArrayList<Class<?>> configClassList = new CopyOnWriteArrayList<Class<?>>();

	// preDestory method cache
	protected ConcurrentHashMap<String, Method> preDestoryMethodCache = new ConcurrentHashMap<String, Method>();

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

	public String getBoxIdentity() {
		return boxIdentity;
	}

	public <T> T getBean(Class<?> clazz) {
		return BeanBoxUtils.getBoxInstance(clazz, this).getBean();
	}

	public BeanBoxContext addConfig(Class<?> configClass) {
		configClassList.add(configClass);
		return this;
	}

	/**
	 * When close() method be called, call all preDestory() method for all singleTon Bean instances.
	 */
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

	/**
	 * Set AspectjAround, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAspectjAround(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList
				.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "AROUND", false));
	}

	/**
	 * Set AspectjBefore, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAspectjBefore(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList
				.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "BEFORE", false));
	}

	/**
	 * Set AspectjAfterReturning, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAspectjAfterReturning(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName,
				"AFTERRETURNING", false));
	}

	/**
	 * Set AspectjAfterThrowing, ClassNameReg and methodNameReg use java Regex
	 */
	public void setAspectjAfterThrowing(String classNameReg, String methodNameReg, BeanBox adviceBeanBox,
			String adviceAroundMethodName) {
		advisorList.add(new Advisor(classNameReg, methodNameReg, adviceBeanBox, adviceAroundMethodName, "AFTERTHROWING",
				false));
	}
}