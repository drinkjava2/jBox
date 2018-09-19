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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBoxContext;
import com.github.drinkjava2.jbeanbox.JBEANBOX;
import com.github.drinkjava2.jbeanbox.annotation.AOP;

/**
 * Annotation configuration AOP test
 * 
 * @author Yong Zhu
 * @since 2.4.8
 *
 */
public class AnnotationAopTest {

	@Before
	public void init() {
		BeanBoxContext.reset();
	}

	public static class Interceptor1 implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "1";
			return invocation.proceed();
		}
	}

	public static class Interceptor2 implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "2";
			return invocation.proceed();
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	@AOP
	public static @interface MyAop1 {
		public Class<?> value() default Interceptor1.class;

		public String method() default "setNa*";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	@AOP
	public static @interface MyAop2 {
		public Class<?> value() default Interceptor2.class;
	}

	@MyAop1
	public static class AopDemo1 {
		String name;
		String address;

		public void setName(String name) {
			this.name = name;
		}

		@MyAop2
		public void setAddress(String address) {
			this.address = address;
		}
	}

	@Test
	public void aopTest1() {
		AopDemo1 demo = JBEANBOX.getBean(AopDemo1.class);
		demo.setName("--");
		Assert.assertEquals("1", demo.name);
		demo.setAddress("--");
		Assert.assertEquals("2", demo.address);
	}
}
