/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jbeanbox.aop;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.github.drinkjava2.cglib3_2_0.proxy.Enhancer;
import com.github.drinkjava2.cglib3_2_0.proxy.Factory;
import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.BeanBoxContext;

/**
 * ProxyBeanUtils use Objenesis and Cglib to create AOP proxy bean
 * 
 * @author Yong Zhu
 * @since 2.4
 *
 */
public class ProxyBeanUtils {// NOSONAR
	private static final Objenesis objenesis = new ObjenesisStd(true);

	public static Object changeBeanToProxy(Object bean, BeanBox box, BeanBoxContext ctx) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(bean.getClass());
		if (box.getConstructorParams() != null && box.getConstructorParams().length > 0) {
			BeanBox[] boxes = box.getConstructorParams();
			Class<?>[] argsTypes = new Class<?>[boxes.length];
			Object[] realArgsValue = new Object[boxes.length];
			for (int i = 0; i < boxes.length; i++) {
				argsTypes[i] = boxes[i].getType();
				Object realValue = ctx.getBean(boxes[i]);
				if (realValue != null && realValue instanceof String)
					realValue = ctx.getValueTranslator().translate((String) realValue, boxes[i].getType());
				realArgsValue[i] = realValue;
			}

//			enhancer.setCallbackType(ProxyBean.class);
//			Class<?> proxyClass = enhancer.createClass();
//			Object proxyInstance = objenesis.newInstance(proxyClass);
//			if (proxyInstance != null) {
//				((Factory) proxyInstance).setCallback(0, new ProxyBean(bean, box, ctx));
//				return proxyInstance;
//			} else 
//			
			{
				enhancer.setCallback(new ProxyBean(bean, box, ctx));
				return enhancer.create(argsTypes, realArgsValue);
			}
		} else {
			enhancer.setCallback(new ProxyBean(bean, box, ctx));
			return enhancer.create();
		}
	}

}
