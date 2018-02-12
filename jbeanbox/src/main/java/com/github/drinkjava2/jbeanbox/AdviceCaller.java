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
package com.github.drinkjava2.jbeanbox;

import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aopalliance.intercept.MethodInvocation;

import com.github.drinkjava2.cglib3_2_0.proxy.MethodProxy;

/**
 * Advice Caller
 * 
 * @author Yong Zhu
 * @since 2.4
 *
 */
class AdviceCaller {
	protected final Object proxy;
	protected final Object target;
	protected final Method method;
	protected Object[] args;
	private MethodProxy cgLibMethodProxy;
	CopyOnWriteArrayList<Advisor> myAdvisors;
	protected int currentAdvisorIndex = -1;

	/**
	 * Create an AdviceCaller
	 * 
	 * @param proxyBean
	 * @param target
	 * @param method
	 * @param arguments
	 * @param cgLibMethodProxy
	 * @param myAdvisors
	 */
	protected AdviceCaller(Object proxyBean, Object target, Method method, Object[] arguments,
			MethodProxy cgLibMethodProxy, CopyOnWriteArrayList<Advisor> myAdvisors) {
		this.proxy = proxyBean;
		this.target = target;
		this.method = method;
		this.args = arguments;
		this.myAdvisors = myAdvisors;
		this.cgLibMethodProxy = cgLibMethodProxy;
	}

	public static Object invoke(String methodType, Method method, Object advice, Object... args) {
		try {
			return method.invoke(advice, args);
		} catch (Exception e) {
			throw new BeanBoxException("Exception happen in AOP " + methodType + " method '" + method.getName() + "'",
					e);
		}
	}

	/**
	 * Check and run the next advisor, first one no need check because already
	 * checked
	 */
	public Object callNextAdvisor() throws Throwable {// NOSONAR
		if (this.currentAdvisorIndex >= this.myAdvisors.size() - 1)
			return cgLibMethodProxy.invokeSuper(target, args);
		Advisor advisor = myAdvisors.get(++this.currentAdvisorIndex);
		if (advisor.match(target.getClass().getName(), method.getName())) {
			Object advice = advisor.adviceBeanBox.getBean();
			if (advisor.isAOPAlliance) {// AOP alliance type advice
				if ("AROUND".equals(advisor.adviceType)) {
					// public Object doAround(MethodInvocation caller) throws Throwable
					Method m = advice.getClass().getMethod(advisor.adviceMethodName,
							new Class[] { MethodInvocation.class });
					Object result = invoke("AROUND", m, advice, new AopAllianceInvocation(target, method, args, this));
					return result;
				} else if ("BEFORE".equals(advisor.adviceType)) {
					// public void before(Method method, Object[] args, Object target) throws
					// Throwable
					Method m = advice.getClass().getMethod(advisor.adviceMethodName,
							new Class[] { Method.class, Object[].class, Object.class });
					invoke("BEFORE", m, advice, new Object[] { method, args, target });
					return callNextAdvisor();
				} else if ("AFTERRETURNING".equals(advisor.adviceType)) {
					// public void afterReturning(Object result, Method method, Object[] args,
					// Object target)
					Object result = callNextAdvisor();
					Method m = advice.getClass().getMethod(advisor.adviceMethodName,
							new Class[] { Object.class, Method.class, Object[].class, Object.class });
					invoke("AFTERRETURNING", m, advice, new Object[] { result, method, args, target });
					return result;
				} else if ("AFTERTHROWING".equals(advisor.adviceType)) {
					// public void afterThrowing(Method method, Object[] args, Object target,
					// Exception ex)
					// Detai see org.springframework.aop.ThrowsAdvice, here only implemented 4
					// arguments
					try {// NOSONAR
						return callNextAdvisor();
					} catch (Exception ex) {
						Method m = advice.getClass().getMethod(advisor.adviceMethodName,
								new Class[] { Method.class, Object[].class, Object.class, Exception.class });
						invoke("AFTERTHROWING", m, advice, new Object[] { method, args, target, ex });
						throw ex;
					}
				}
			} else {
				// from 2.4.3 deleted uncommon used AspectJ support to avoid the 3rd party
				// dependency
				throw new BeanBoxException("BeanBox only support AOPAlliance Advice");
			}
			throw new BeanBoxException("BeanBox AdviceType not support error: " + advisor.adviceType);
		} else
			return callNextAdvisor();
	}
}