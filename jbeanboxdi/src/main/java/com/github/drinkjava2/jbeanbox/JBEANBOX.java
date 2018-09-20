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

/**
 * JBEANBOX store a default globalBeanBoxContext, and have public static method
 * to access it
 * 
 * @author Yong Zhu
 * @since 2.4
 *
 */
public class JBEANBOX {// NOSONAR

	public static BeanBoxContext bctx() {
		return BeanBoxContext.globalBeanBoxContext;
	}

	public static void reset() {
		BeanBoxContext.reset();
	}

	public static void close() {
		bctx().close();
	}

	public static <T> T getBean(Object target) {
		return bctx().getBean(target);
	}

	public static <T> T getBean(Object target, boolean required) {
		return bctx().getBean(target, required);
	}

	public static <T> T getInstance(Class<T> clazz) {
		return bctx().getInstance(clazz);
	}

	public static <T> T getInstance(Class<T> clazz, boolean required) {
		return bctx().getInstance(clazz, required);
	}

	public static BeanBoxContext bind(Object shortcut, Object target) {
		return bctx().bind(shortcut, target);
	}

	public static BeanBox getBeanBox(Class<?> clazz) {
		return BeanBoxUtils.getUniqueBeanBox(BeanBoxContext.globalBeanBoxContext, clazz);
	}

	public static BeanBox autowired() {
		return new BeanBox().setTarget(EMPTY.class);
	}

	/** Equal to "@INJECT" annotation */
	public static BeanBox inject() {
		return new BeanBox().setTarget(EMPTY.class);
	}

	/** Equal to "@INJECT" annotation */
	public static BeanBox inject(Object target) {
		return new BeanBox().setTarget(target);
	}

	/** Equal to "@INJECT" annotation */
	public static BeanBox inject(Object target, boolean pureValue, boolean required) {
		return new BeanBox().setTarget(target).setPureValue(pureValue).setRequired(required);
	}

	/** Equal to "@VALUE" annotation */
	public static BeanBox value(Object value) {
		return new BeanBox().setTarget(value).setPureValue(true).setRequired(true);
	}

	/** Equal to "@VALUE" annotation */
	public static BeanBox value(Object value, boolean pureValue, boolean required) {
		return new BeanBox().setTarget(value).setPureValue(pureValue).setRequired(required);
	}

}
