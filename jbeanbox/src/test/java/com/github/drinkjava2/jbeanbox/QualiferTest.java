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
package com.github.drinkjava2.jbeanbox;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.github.drinkjava2.jbeanbox.annotation.COMPONENT;

/**
 * Qualifer annotation Test
 * 
 * @author Yong Zhu
 * @since 2.5.0
 *
 */
public class QualiferTest {

	@Before
	public void init() {
		JBEANBOX.reset();
	}

	protected void componentNameTest_____________________() {
	}

	@Component
	public static interface A {
	}

	@COMPONENT("a1")
	public static class A1 implements A {
	}

	@Service
	public static class A2 implements A {
	}

	@Repository
	public static class A3 implements A {
	}

	@Controller("a-4")
	public static class A4 implements A {
	}

	@Test
	public void testComponentClass() {
		Assert.assertEquals(A1.class, JBEANBOX.getInstance(A1.class).getClass());
		Assert.assertEquals(A2.class, JBEANBOX.getInstance(A2.class).getClass());
		Assert.assertEquals(A3.class, JBEANBOX.getInstance(A3.class).getClass());
		Assert.assertEquals(A4.class, JBEANBOX.getInstance(A4.class).getClass());
	}

	@Test
	public void testComponentName() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		Assert.assertEquals(A1.class, JBEANBOX.getObject("a1").getClass());
		Assert.assertEquals(A2.class, JBEANBOX.getObject("a2").getClass());
		Assert.assertEquals(A3.class, JBEANBOX.getObject("a3").getClass());
		Assert.assertEquals(A4.class, JBEANBOX.getObject("a-4").getClass());
	}

	protected void qualifierTests_____________________() {
	}

	public enum Color {
		RED, BLACK, TAN
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	public @interface Leather {
		Color color() default Color.RED;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	public @interface Leather2 {
		Color color() default Color.TAN;
	}

}
