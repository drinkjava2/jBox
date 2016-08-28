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
		return getBeanBox(null, clazz, null, null, this, false).getBean();
	}

	public <T> T getBean(Class<?> clazz, Class<?> configClass) {
		return getBeanBox(null, clazz, configClass, null, this, false).getBean();
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

	/**
	 * Create BeanBox instance for clazz and inject context to it, configClazz is given first find BeanBox in it
	 */
	public static BeanBox getBeanBox(Class<?> ownerClass, Class<?> clazz, Class<?> annotationClass, String fieldName,
			BeanBoxContext context, boolean isMustFind) {
		if (Object.class.equals(annotationClass))
			annotationClass = null;
		Class<?> box = null;
		if (annotationClass != null) { // getBeanBox(A.class, AnnoConfig.class)
			if (BeanBox.class.isAssignableFrom(annotationClass))
				box = annotationClass;// #1 AnnoBox
			if (box == null && clazz != null)
				box = BeanBoxUtils
						.checkIfExist(annotationClass.getName() + "$" + clazz.getSimpleName() + context.boxIdentity);// #2
			// Anno$ABox
			if (box == null)
				box = BeanBoxUtils.checkIfExist(annotationClass.getName() + "$"
						+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + context.boxIdentity);// #3
			// Anno$FieldBox
		} else {// getBeanBox(A.class)
			if (clazz == null)
				BeanBoxUtils.throwError(null, "BeanBox getBeanBox error! target class not set");
			if (BeanBox.class.isAssignableFrom(clazz))
				box = clazz;// #3.5 A.class is BeanBox
			if (box == null)
				box = BeanBoxUtils.checkIfExist(clazz.getName() + context.boxIdentity);// #4 ABox
			if (box == null)
				box = BeanBoxUtils.checkIfExist(clazz.getName() + "$" + clazz.getSimpleName() + context.boxIdentity);// #4.5
			if (box == null && ownerClass != null)
				box = BeanBoxUtils.checkIfExist(
						ownerClass.getName() + context.boxIdentity + "$" + clazz.getSimpleName() + context.boxIdentity);// #5
			if (box == null && ownerClass != null && !BeanBoxUtils.isEmptyStr(fieldName))
				box = BeanBoxUtils.checkIfExist(
						ownerClass.getName() + context.boxIdentity + "$" + fieldName + context.boxIdentity);// #5.5
			// OwerBox$FieldBox
			if (box == null) {
				for (Class<?> configs : context.configClassList) {
					box = BeanBoxUtils
							.checkIfExist(configs.getName() + "$" + clazz.getSimpleName() + context.boxIdentity);// #6
																													// Config$ABox
					if (box != null)
						break;
					box = BeanBoxUtils.checkIfExist(configs.getName() + "$" + fieldName.substring(0, 1).toUpperCase()
							+ fieldName.substring(1) + context.boxIdentity);// #7
																			// Config$FieldBox
					if (box != null)
						break;
				}
			}
		}
		if (isMustFind && box == null)
			BeanBoxUtils.throwError(null, "BeanBox getBeanBox error! Required BeanBox not found, class=" + clazz);
		if (box == null)
			return new BeanBox(clazz); // wrap it to a BeanBox, class should have a 0 parameter constructor
		BeanBox beanbox = BeanBoxUtils.createBeanOrBoxInstance(box, context);
		if (beanbox.getClassOrValue() == null)
			beanbox.setClassOrValue(clazz);
		return beanbox;
	}
}