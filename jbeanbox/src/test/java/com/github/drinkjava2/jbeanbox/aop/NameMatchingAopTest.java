/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jbeanbox.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBoxContext;
import com.github.drinkjava2.jbeanbox.JBEANBOX;

/**
 * Annotation configuration AOP test
 * 
 * @author Yong Zhu
 * @since 2.4.8
 *
 */
public class NameMatchingAopTest {

	public static class AopDemo1 {
		String name;
		String address;
		String email;

		public void setName(String name) {
			this.name = name;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	public static class ContextAOP implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "bar";
			return invocation.proceed();
		}
	}

	@Test
	public void aopTest1() {
		BeanBoxContext.reset();
		// Only setName and setAddress be AOPed.
		JBEANBOX.bctx().addContextAop(ContextAOP.class, AopDemo1.class, "setN*|*Address");
		AopDemo1 demo = JBEANBOX.getBean(AopDemo1.class);
		demo.setName("foo");
		Assert.assertEquals("bar", demo.name);
		demo.setAddress("foo");
		Assert.assertEquals("bar", demo.address);
		demo.setEmail("foo");
		Assert.assertEquals("foo", demo.email);
	}

}
