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
package com.github.drinkjava2.jbeanbox.aop;

import static com.github.drinkjava2.jbeanbox.ReflectionUtils.isEqualsMethod;
import static com.github.drinkjava2.jbeanbox.ReflectionUtils.isHashCodeMethod;
import static com.github.drinkjava2.jbeanbox.ReflectionUtils.isToStringMethod;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.github.drinkjava2.cglib3_2_0.proxy.Callback;
import com.github.drinkjava2.cglib3_2_0.proxy.MethodInterceptor;
import com.github.drinkjava2.cglib3_2_0.proxy.MethodProxy;
import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.BeanBoxContext;
import com.github.drinkjava2.jbeanbox.BeanBoxException;

/**
 * ProxyBean to build a AopChain, AopChain build a TempMethodInvocation
 * 
 * @author Yong Zhu
 * @since 2.4
 *
 */
@SuppressWarnings("all")
class ProxyBean implements MethodInterceptor, Callback {
	protected Object[] bean_box_ctx;

	protected ProxyBean(Object bean, BeanBox box, BeanBoxContext ctx) {
		bean_box_ctx = new Object[] { bean, box, ctx };
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy cgLibMethodProxy) throws Throwable {
		if (isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method))
			return cgLibMethodProxy.invokeSuper(obj, args);
		BeanBox box = (BeanBox) bean_box_ctx[1];
		if (box == null || box.getMethodAops() == null || box.getMethodAops().isEmpty())
			return cgLibMethodProxy.invokeSuper(obj, args);
		List<Object> interceptors = box.getMethodAops().get(method);
		if (interceptors == null || interceptors.isEmpty())
			return cgLibMethodProxy.invokeSuper(obj, args);
		else
			return new AopChain(obj, method, args, cgLibMethodProxy, bean_box_ctx, interceptors).callNext();
	}

	public static class AopChain {
		Object proxy;
		Method method;
		Object[] args;
		MethodProxy cgLibMethodProxy;
		Object[] bean_box_ctx;
		List<Object> interceptors;
		int index = -1; // the AOP chain index

		public AopChain(Object proxy, Method method, Object[] args, MethodProxy cgLibMethodProxy, Object[] bean_box_ctx,
				List<Object> interceptors) {
			this.proxy = proxy;
			this.method = method;
			this.cgLibMethodProxy = cgLibMethodProxy;
			this.bean_box_ctx = bean_box_ctx;
			this.interceptors = interceptors;
		}

		public Object callNext() throws Throwable {
			index++;
			if (index > interceptors.size() - 1)
				return cgLibMethodProxy.invokeSuper(proxy, args);
			else {
				BeanBoxContext ctx = (BeanBoxContext) bean_box_ctx[2];
				Object target = interceptors.get(index);
				org.aopalliance.intercept.MethodInterceptor inter = ctx.getBean(target);
				BeanBoxException.assureNotNull(inter);
				MethodInvocation invocation = new TempMethodInvocation(proxy, method, args, this);
				return inter.invoke(invocation);
			}
		}
	}

	//@formatter:off
	public static class TempMethodInvocation implements MethodInvocation {
		private final Object proxy;
		private final Method method;
		private final Object[] args;
		private final AopChain chain;

		protected TempMethodInvocation(Object proxy, Method method, Object[] args, AopChain chain) {
			this.proxy = proxy;	this.method = method;this.args = args;this.chain = chain;
		}
		@Override
		public final Object getThis() {	return this.proxy;	}
		@Override
		public final AccessibleObject getStaticPart() {	return this.method;	}
		@Override
		public final Method getMethod() { return this.method;	}
		@Override
		public final Object[] getArguments() { return this.args != null ? this.args : new Object[0];}
		@Override
		public Object proceed() throws Throwable { return chain.callNext();}
	}
}