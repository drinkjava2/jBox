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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.drinkjava2.jbeanbox.annotation.AOP;
import com.github.drinkjava2.jbeanbox.annotation.INJECT;
import com.github.drinkjava2.jbeanbox.annotation.NAMED;
import com.github.drinkjava2.jbeanbox.annotation.POSTCONSTRUCT;
import com.github.drinkjava2.jbeanbox.annotation.PREDESTROY;
import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;
import com.github.drinkjava2.jbeanbox.annotation.QUALIFILER;
import com.github.drinkjava2.jbeanbox.annotation.VALUE;

/**
 * BeanBoxUtils store public static methods used inside of this project
 * 
 * @author Yong Zhu
 * @since 2.4.7
 */
public class BeanBoxUtils {// NOSONAR

	/** Get a class BeanBox which sington property is ture */
	public static BeanBox getSingletonBeanBox(BeanBoxContext ctx, Class<?> clazz) {
		return getBeanBox(ctx, clazz, true);
	}

	/** Get a class BeanBox which sington property is false */
	public static BeanBox getPrototypeBeanBox(BeanBoxContext ctx, Class<?> clazz) {
		return getBeanBox(ctx, clazz, false);
	}

	/** Get a class BeanBox which sington property determined by annotation */
	public static BeanBox getBeanBox(BeanBoxContext ctx, Class<?> clazz) {
		return getBeanBox(ctx, clazz, null);
	}

	/**
	 * Get BeanBox for class, prototype can be null/true/false represents
	 * default/prototype/sington 3 type beanbox
	 */
	private static BeanBox getBeanBox(BeanBoxContext ctx, Class<?> clazz, Boolean singleton) {
		BeanBoxException.assureNotNull(clazz, "Target class can not be null");
		BeanBox box = ctx.beanBoxCache.get(clazz);
		if (box != null) {
			if (singleton == null)
				return box;
			if (singleton && box.isSingleton())
				return box;
			return box.newCopy().setSingleton(singleton);
		}
		if (BeanBox.class.isAssignableFrom(clazz)) // not found beanbox
			try {
				box = (BeanBox) clazz.newInstance();
				if (box.singleton == null)
					box.singleton = true;
			} catch (Exception e) {
				BeanBoxException.throwEX(e);
			}
		else
			box = doCreateBeanBox(ctx, clazz);
		if (box.beanClass != null && PrototypeBean.class.isAssignableFrom(box.beanClass))// NOSONAR
			box.setSingleton(false);
		ctx.beanBoxCache.put(clazz, box);
		return box;
	}

	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... paramTypes) {// NOSONAR
		try {
			return clazz.getConstructor(paramTypes);
		} catch (SecurityException e) {
			throw new IllegalStateException("Security exception found for Constructor: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Constructor not found: " + e.getMessage());
		}
	}

	protected static void belowArePrivateStaticMethods__________________________() {// NOSONAR
	}

	/** Read Bean annotations to build a BeanBox instance */
	private static BeanBox doCreateBeanBox(BeanBoxContext ctx, Class<?> clazz) {// NOSONAR
		BeanBox box = new BeanBox();
		box.setBeanClass(clazz);
		box.setSingleton(true); // for static class default set to singleton

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
					if ("value".equals(entry.getKey())) {// NOSONAR
						if ("prototype".equalsIgnoreCase(String.valueOf(entry.getValue())))
							box.setSingleton(false);
						else if ("singleton".equalsIgnoreCase(String.valueOf(entry.getValue())))
							box.setSingleton(true);
						else
							BeanBoxException.throwEX("'prototype' or 'singleton' required in @Scope annotation");
					}
		}

		// ======== Class inject, if @INJECT, @Qualifiler put on class
		BeanBox v = getInjectBoxFromAnno(clazz, allowSpringJsrAnno);
		if (v != null) 
			copyBoxValues(v, box);

		// ======== AOP annotated annotations on class
		Annotation[] annos = clazz.getAnnotations();
		for (Annotation anno : annos) {
			if (anno.annotationType().isAnnotationPresent(AOP.class)) {
				Map<String, Object> annoMap = changeAnnotationValuesToMap(anno);
				Object aop = annoMap.get("value");
				String methodNameRule = (String) annoMap.get("method");// name must be method
				if (methodNameRule != null && methodNameRule.length() > 0 && aop != null)
					box.addBeanAop(aop, methodNameRule);
			}
		}

		// ========== Constructor inject
		Constructor<?>[] constrs = clazz.getConstructors();
		for (Constructor<?> constr : constrs) {
			v = getInjectBoxFromAnno(constr, allowSpringJsrAnno);
			if (v != null) {
				box.setBeanClass(clazz);// anyway set beanClass first
				if (v.target != null && AUTOWIRE.class != v.target) {// 1 parameter only
					BeanBox inject = new BeanBox();
					copyBoxValues(v, inject);					
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
		// @INJECT annotations on fields include super class's
		for (Field f : ReflectionUtils.getSelfAndSuperClassFields(clazz)) {
			v = getInjectBoxFromAnno(f, allowSpringJsrAnno);
			if (v != null) {
				box.checkOrCreateFieldInjects();
				BeanBox inject = new BeanBox();
				inject.setTarget(v.target);
				inject.setPureValue(v.pureValue);
				inject.setRequired(v.required);
				inject.setType(f.getType());
				inject.setQualifierAnno(v.qualifierAnno);
				inject.setQualifierValue(v.qualifierValue);
				ReflectionUtils.makeAccessible(f);
				box.getFieldInjects().put(f, inject);
			}
		}

		// @INJECT annotations on methods include super class's
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
		for (Method m : methods) {
			// ========== @PostConstruct and @PreDestory
			if (m.getAnnotation(POSTCONSTRUCT.class) != null || m.getAnnotation(PostConstruct.class) != null) {
				if (m.getParameterTypes().length > 0)
					BeanBoxException.throwEX("In jBeanBox, PostConstruct should have no parameter.");
				ReflectionUtils.makeAccessible(m);
				box.setPostConstruct(m);
			}
			if (m.getAnnotation(PREDESTROY.class) != null || m.getAnnotation(PreDestroy.class) != null) {
				if (m.getParameterTypes().length > 0)
					BeanBoxException.throwEX("In jBeanBox, PostConstruct should have no parameter.");
				ReflectionUtils.makeAccessible(m);
				box.setPreDestroy(m);
			}

			// ========== AOP about annotation =========
			Annotation[] mtdAnnos = m.getAnnotations();
			for (Annotation anno : mtdAnnos)
				if (anno.annotationType().isAnnotationPresent(AOP.class)) {
					Map<String, Object> annoMap = changeAnnotationValuesToMap(anno);
					Object aop = annoMap.get("value");
					if (aop != null)
						box.addMethodAop(aop, m);
				}

			// =========== method inject annotation ==============
			v = getInjectBoxFromAnno(m, allowSpringJsrAnno);
			if (v != null) {
				ReflectionUtils.makeAccessible(m);
				BeanBox oneParam = new BeanBox();
				oneParam.setTarget(v.target);
				oneParam.setPureValue(v.pureValue);
				oneParam.setRequired(v.required);
				boolean haveOneParameter = v.target != null && AUTOWIRE.class != v.target;
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

	private static void copyBoxValues(BeanBox from, BeanBox to) {
		to.setTarget(from.target);
		to.setPureValue(from.pureValue);
		to.setRequired(from.required);
		to.setQualifierAnno(from.qualifierAnno);
		to.setQualifierValue(from.qualifierValue);
	}

	/** Get wanted Inject info from target annotation */
	private static BeanBox getInjectBoxFromAnno(Object target, boolean allowSpringJsrAnno) {
		Annotation[] anno = getAnnotations(target);
		return getInjectBoxFromAnnos(anno, allowSpringJsrAnno);
	}

	/** Get a BeanBox instance from annotation array */
	@SuppressWarnings("unchecked")
	private static BeanBox getInjectBoxFromAnnos(Annotation[] anno, boolean allowSpringJsrAnno) {// NOSONAR
		BeanBox box = null;
		for (Annotation a : anno) {
			Class<? extends Annotation> type = a.annotationType();
			if (INJECT.class.equals(type)) {
				INJECT i = (INJECT) a;
				box = new BeanBox().setTarget(i.value()).setRequired(i.required()).setPureValue(i.pureValue());
			} else if (VALUE.class.equals(type))
				box = new BeanBox().setTarget(((VALUE) a).value()).setRequired(true).setPureValue(true);
			else if (allowSpringJsrAnno) {
				if (Inject.class.equals(type))
					box = new BeanBox().setTarget(AUTOWIRE.class).setPureValue(false);
				if (Autowired.class.equals(type))
					box = new BeanBox().setTarget(AUTOWIRE.class).setRequired(((Autowired) a).required());
			}
		}
		for (Annotation a : anno) {
			Class<? extends Annotation> type = a.annotationType();
			if (BeanBoxUtils.ifSameOrChildAnno(type, NAMED.class, QUALIFILER.class) // if have qualifiler anno?
					|| (allowSpringJsrAnno && BeanBoxUtils.ifSameOrChildAnno(type, Named.class, Qualifier.class,
							org.springframework.beans.factory.annotation.Qualifier.class))) {
				Map<String, Object> v = BeanBoxUtils.changeAnnotationValuesToMap(a);
				if (v.size() > 1)
					BeanBoxException
							.throwEX("jBeanBox does not support multiple property in Qualifier annotation: " + type);
				if (box == null)
					box = new BeanBox();
				box.setQualifierAnno(type).setQualifierValue(v.isEmpty() ? null : v.values().iterator().next());
			}
		}
		return box;
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
			BeanBox v = getInjectBoxFromAnnos(annos, allowSpringJsrAnno);
			BeanBox inject = new BeanBox();
			if (v != null) { // if parameter has annotation
				inject.setTarget(v.target);
				inject.setPureValue(v.pureValue);
				inject.setRequired(v.required);
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
				return changeAnnotationValuesToMap(a);
		}
		return null;
	}

	protected static boolean ifSameOrChildAnno(Class<? extends Annotation> annoType,
			@SuppressWarnings("unchecked") Class<? extends Annotation>... annoTypes) {
		for (Class<? extends Annotation> a : annoTypes)
			if (annoType.equals(a) || annoType.isAnnotationPresent(a))
				return true;
		return false;
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
	protected static Map<String, Object> changeAnnotationValuesToMap(Annotation annotation) {
		Map<String, Object> result = new HashMap<>();
		for (Method method : annotation.annotationType().getDeclaredMethods())
			try {
				result.put(method.getName(), method.invoke(annotation, (Object[]) null));
			} catch (Exception e) {// NOSONAR
			}
		return result;
	}

	/**
	 * If aop is a instance of Aop alliance Interceptor, wrap it to a BeanBox and
	 * set as purevalue, otherwise direct return it (class or BeanBox)
	 */
	protected static Object checkAOP(Object aop) {
		if (aop != null && aop instanceof MethodInterceptor)
			return new BeanBox().setTarget(aop).setPureValue(true);
		else
			return aop;
	}

	/**
	 * If param is class, wrap it to BeanBox, if param is BeanBox instance, direct
	 * return it, otherwise wrap it as pure Value BeanBox
	 */
	protected static BeanBox wrapParamToBox(Object param) {
		if (param != null) {
			if (param instanceof Class)
				return new BeanBox().setTarget(param);
			if (param instanceof BeanBox)
				return (BeanBox) param;
		}
		return new BeanBox().setAsValue(param);
	}

}
