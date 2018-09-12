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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.drinkjava2.jbeanbox.annotation.INJECT;
import com.github.drinkjava2.jbeanbox.annotation.CONS;
import com.github.drinkjava2.jbeanbox.annotation.POSTCONSTRUCT;
import com.github.drinkjava2.jbeanbox.annotation.PREDESTROY;
import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;

/**
 * BeanBoxUtils translate class to BeanBox instance and cache them
 * 
 * @author Yong Zhu
 * @since 2.4.7
 */
public class BeanBoxUtils {// NOSONAR

	/**
	 * Translate a BeanBox class or normal class to a readOnly BeanBox instance
	 */
	@SuppressWarnings("unchecked")
	public static BeanBox getUniqueBeanBox(BeanBoxContext ctx, Class<?> clazz) {
		BeanBoxException.assureNotNull(clazz, "Target class can not be null");
		BeanBox box = ctx.beanBoxMetaCache.get(clazz);
		if (box != null)
			return box;
		if (BeanBox.class.isAssignableFrom(clazz))
			try {
				box = (BeanBox) clazz.newInstance();
				if (box.singleton == null)
					box.singleton = true;
				// ===== create method and config method
				Method mthd = ctx.checkAndReturnCreateMethod((Class<BeanBox>) clazz);
				if (mthd != null)
					box.setCreateMethod(mthd);
				mthd = ctx.checkAndReturnConifgMethod((Class<BeanBox>) clazz);
				if (mthd != null)
					box.setConfigMethod(mthd);

			} catch (Exception e) {
				BeanBoxException.throwEX(e);
			}
		else
			box = doCreateBeanBox(ctx, clazz);
		ctx.beanBoxMetaCache.put(clazz, box);
		return box;
	}

	/** Invoke method and catch exception to BeanBoxException */
	public static Object invokeMethodAndCatchEX(Method method, Object obj, Object... params) {// NOSONAR
		try {
			return method.invoke(obj, params);
		} catch (Exception e) {
			return BeanBoxException.throwEX(e);
		}
	}

	/** Get constructor of class and catch exception to BeanBoxException */
	public static Constructor<?> getConstructAndCatchEX(Class<?> clazz, Class<?>... paramTypes) {// NOSONAR
		try {
			return clazz.getDeclaredConstructor(paramTypes);
		} catch (Exception e) {
			return (Constructor<?>) BeanBoxException.throwEX(e);// NOSONAR
		}
	}

	/** Get method of class and catch exception to BeanBoxException */
	public static Method getMethodAndCatchEX(Class<?> clazz, String name, Class<?>... paramTypes) {// NOSONAR
		try {
			return clazz.getDeclaredMethod(name, paramTypes);
		} catch (Exception e) {
			return (Method) BeanBoxException.throwEX(e);// NOSONAR
		}
	}

	/** Get Field of class and catch exception to BeanBoxException */
	public static Field getFieldAndCatchEX(Class<?> clazz, String name) {// NOSONAR
		try {
			return clazz.getDeclaredField(name);
		} catch (Exception e) {
			return (Field) BeanBoxException.throwEX(e);// NOSONAR
		}
	}

	protected static void belowArePrivateStaticMethods__________________________() {// NOSONAR
	}

	/** Read Bean annotations to build a BeanBox instance */
	private static BeanBox doCreateBeanBox(BeanBoxContext ctx, Class<?> clazz) {// NOSONAR
		BeanBox box = new BeanBox();
		box.setBeanClass(clazz);
		box.setSingleton(true);// Annotated class, default is singleton

		if (!ctx.isAllowAnnotation())
			return box;
		boolean allowSpringJsrAnno = ctx.allowSpringJsrAnnotation;

		// ========= singleton or prototype
		if (checkAnnoExist(clazz, PROTOTYPE.class))
			box.setSingleton(false);
		else if (allowSpringJsrAnno) {
			Map<String, Object> m = getAnnoAsMap(clazz, "org.springframework.context.annotation.Scope");
			if (m != null)
				for (Entry<String, Object> entry : m.entrySet())
					if ("value".equals(entry.getKey())) {
						if ("prototype".equalsIgnoreCase(String.valueOf(entry.getValue())))
							box.setSingleton(false);
						else if ("singleton".equalsIgnoreCase(String.valueOf(entry.getValue())))
							box.setSingleton(true);
						else
							BeanBoxException.throwEX("'prototype' or 'singleton' required in @Scope annotation");
					}
		}

		// ======== Class inject, if @INJECT, @PARAM put on class
		Object[] v = getInjectAnnotationAsArray(clazz, allowSpringJsrAnno);
		if (v != null) {
			box.setTarget(v[0]);
			box.setConstant((Boolean) v[1]);
			box.setRequired((Boolean) v[2]);
		}

		// ========== Constructor inject
		Constructor<?>[] constrs = clazz.getConstructors();
		for (Constructor<?> constr : constrs) {
			v = getInjectAnnotationAsArray(constr, allowSpringJsrAnno);
			if (v != null) {
				if (v[0] != null && EMPTY.class != v[0]) {// 1 parameter only
					BeanBox inject = new BeanBox();
					inject.setTarget(v[0]);
					inject.setConstant((Boolean) v[1]);
					inject.setRequired((Boolean) v[2]);
					inject.setType(constr.getParameterTypes()[0]);
					box.setConstructor(constr);
					box.setConstructorParams(new BeanBox[] { inject });
				} else { // no or many parameter
					BeanBox[] paramInjects = getParameterInjectAsBeanBoxArray(constr, allowSpringJsrAnno);
					box.setConstructor(constr);
					box.setConstructorParams(paramInjects);
				}
			}
		}

		// =================Field inject=================
		// @INJECT and values
		for (Field f : clazz.getDeclaredFields()) {
			v = getInjectAnnotationAsArray(f, allowSpringJsrAnno);
			if (v != null) {
				box.checkOrCreateFieldInjects();
				BeanBox inject = new BeanBox();
				inject.setTarget(v[0]);
				inject.setConstant((Boolean) v[1]);
				inject.setRequired((Boolean) v[2]);
				inject.setType(f.getType());
				makeAccessible(f);
				box.getFieldInjects().put(f, inject);
			}
		}

		Method[] methods = clazz.getDeclaredMethods();
		for (Method m : methods) {
			// ========== @PostConstruct and @PreDestory
			if (m.getAnnotation(POSTCONSTRUCT.class) != null || m.getAnnotation(PostConstruct.class) != null) {
				if (m.getParameterTypes().length > 0)
					BeanBoxException.throwEX("In jBeanBox, PostConstruct should have no parameter.");
				makeAccessible(m);
				box.setPostConstruct(m);
			}
			if (m.getAnnotation(PREDESTROY.class) != null || m.getAnnotation(PreDestroy.class) != null) {
				if (m.getParameterTypes().length > 0)
					BeanBoxException.throwEX("In jBeanBox, PostConstruct should have no parameter.");
				makeAccessible(m);
				box.setPreDestroy(m);
			}

			v = getInjectAnnotationAsArray(m, allowSpringJsrAnno);
			if (v != null) {
				makeAccessible(m);
				BeanBox oneParam = new BeanBox();
				oneParam.setTarget(v[0]);
				oneParam.setConstant((Boolean) v[1]);
				oneParam.setRequired((Boolean) v[2]);
				boolean haveOneParameter = v[0] != null && EMPTY.class != v[0];
				if (haveOneParameter)
					oneParam.setType(m.getParameterTypes()[0]); // set parameter type for 1 parameter
				// @INJECT or @Inject or @Autowired normal method inject
				box.checkOrCreateMethodInjects();
				if (haveOneParameter) // 1 parameter only
					box.getMethodInjects().put(m, new BeanBox[] { oneParam });
				else { // no or many parameter
					BeanBox[] paramInjects = getParameterInjectAsBeanBoxArray(m, allowSpringJsrAnno);
					box.getMethodInjects().put(m, paramInjects);
				}
			}
		}
		return box;

	}

	/**
	 * Get @INJECT or @POSTCONSTRUCT or @PARAM or @PREDESTROY or @PROTOTYPE
	 * annotation values, return Object[3] or null if no above annotations found
	 */
	private static Object[] getInjectAnnotationAsArray(Object target, boolean allowSpringJsrAnno) {
		Annotation[] anno = getAnnotations(target);
		return getInjectAsArray(anno, allowSpringJsrAnno);
	}

	/**
	 * get Inject As Object[4] Array, 0=value 1=isConstant 2=required
	 * 3=annotationType, if not found annotation inject, return null
	 */
	private static Object[] getInjectAsArray(Annotation[] anno, boolean allowSpringJsrAnno) {// NOSONAR
		for (Annotation a : anno) {
			Class<? extends Annotation> type = a.annotationType();
			if (INJECT.class.equals(type))
				return new Object[] { ((INJECT) a).value(), ((INJECT) a).constant(), ((INJECT) a).required(), null };
			if (CONS.class.equals(type))
				return new Object[] { ((CONS) a).value(), ((CONS) a).constant(), ((CONS) a).required(), null };
			if (allowSpringJsrAnno) {
				if (Inject.class.equals(type))
					return new Object[] { EMPTY.class, false, true, null };
				if (Autowired.class.equals(type))
					return new Object[] { EMPTY.class, false, ((Autowired) a).required(), null };
			}
		}
		return null;// NOSONAR
	}

	/** Get Parameter Inject as BeanBox[] Array */
	private static BeanBox[] getParameterInjectAsBeanBoxArray(Object o, boolean allowSpringJsrAnno) {
		Annotation[][] annoss = null;
		Class<?>[] paramTypes = null;
		if (o instanceof Method) {
			annoss = ((Method) o).getParameterAnnotations();
			paramTypes = ((Method) o).getParameterTypes();
		} else if (o instanceof Constructor) {
			annoss = ((Constructor<?>) o).getParameterAnnotations();
			paramTypes = ((Constructor<?>) o).getParameterTypes();
		} else
			return BeanBoxException.throwEX("Only method or Constructor are allowed at here for:" + o);
		BeanBox[] beanBoxes = new BeanBox[annoss.length];
		for (int i = 0; i < annoss.length; i++) {
			Annotation[] annos = annoss[i];
			Object[] v = getInjectAsArray(annos, allowSpringJsrAnno);
			BeanBox inject = new BeanBox();
			if (v != null) { // if parameter has annotation
				inject.setTarget(v[0]);
				inject.setConstant((Boolean) v[1]);
				inject.setRequired((Boolean) v[2]);
				inject.setType(paramTypes[i]);
			} else // if parameter no annotation
				inject.setTarget(paramTypes[i]);
			beanBoxes[i] = inject;
		}
		return beanBoxes;
	}

	/** give a class or Field or Method, return annotations */
	private static Annotation[] getAnnotations(Object targetClass) {
		if (targetClass instanceof Field)
			return ((Field) targetClass).getAnnotations();
		else if (targetClass instanceof Method)
			return ((Method) targetClass).getAnnotations();
		else if (targetClass instanceof Constructor)
			return ((Constructor<?>) targetClass).getAnnotations();
		else if (targetClass instanceof Class)
			return ((Class<?>) targetClass).getAnnotations();
		else
			return BeanBoxException.throwEX("targetClass should be Field, Method, Constructor or Class");
	}

	/** Return all annotations for Class or Field */
	private static Map<String, Object> getAnnoAsMap(Object targetClass, String annoFullName) {
		Annotation[] anno = getAnnotations(targetClass);
		for (Annotation a : anno) {
			Class<? extends Annotation> type = a.annotationType();
			if (annoFullName.equals(type.getName()))
				return changeAnnotationValuesToMap(a, type);
		}
		return null;
	}

	/** Check if annotation exist in Class or Field */
	private static boolean checkAnnoExist(Object targetClass, Class<?> annoClass) {
		Annotation[] anno = getAnnotations(targetClass);
		for (Annotation annotation : anno) {
			Class<? extends Annotation> type = annotation.annotationType();
			if (annoClass.equals(type))
				return true;
		}
		return false;
	}

	/** This used for unknown Annotation, change values to a Map */
	protected static Map<String, Object> changeAnnotationValuesToMap(Annotation annotation,
			Class<? extends Annotation> type) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("AnnotationExist", true);
		for (Method method : type.getDeclaredMethods())
			try {
				result.put(method.getName(), method.invoke(annotation, (Object[]) null));
			} catch (Exception e) {// NOSONAR
			}
		return result;
	}

	/** Make the given method accessible */
	protected static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/** Make the given field accessible */
	protected static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/** Force set field's value */
	protected static void setFieldValue(Object bean, Field f, Object fieldValue) {
		try {
			f.set(bean, fieldValue);
		} catch (IllegalAccessException e) {
			BeanBoxException.throwEX("Fail to write to field:" + f, e);
		}
	}

}
