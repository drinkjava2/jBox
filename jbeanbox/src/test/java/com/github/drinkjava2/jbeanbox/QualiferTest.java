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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.github.drinkjava2.jbeanbox.annotation.COMPONENT;
import com.github.drinkjava2.jbeanbox.annotation.INJECT;
import com.github.drinkjava2.jbeanbox.annotation.NAMED;

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

	protected void qualifierAnnoTests_____________________() {
	}

	public enum Color {
		RED, BLACK, TAN
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER })
	@Qualifier
	public @interface ColorRed {
		Color color() default Color.RED;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER })
	@Qualifier
	public @interface ColorAny {
		Color value() default Color.BLACK;
	}

	public static interface Leather {
	}

	@COMPONENT
	@NAMED("red")
	public static class LeatherRed1 implements Leather {
	}

	@COMPONENT
	@ColorRed
	public static class LeatherRed2 implements Leather {
	}

	@COMPONENT("tan")
	@ColorAny(Color.TAN)
	public static class LeatherTan implements Leather {
	}

	public static class Bean1 {
		@INJECT(required = false)
		@ColorRed
		Leather l1;

		@INJECT(required = false)
		@ColorAny(Color.TAN)
		Leather l2;

		@INJECT(required = false)
		@NAMED("red")
		Leather l3;

		@INJECT(required = false)
		@NAMED("tan")
		Leather l4;

		@INJECT(required = false)
		Leather l5;
	}

	@Test
	public void testQuali1() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		BeanBox box = JBEANBOX.getBeanBox(Bean1.class);
 
		int count = 0;
		for (BeanBox b : box.getFieldInjects().values()) {
			if (ColorRed.class.equals(b.getQualifierAnno())) {
				count++;
				Assert.assertEquals(Color.RED, b.getQualifierValue());
			}
			if (ColorAny.class.equals(b.getQualifierAnno())) {
				count++;
				Assert.assertEquals(Color.TAN, b.getQualifierValue());
			}
			if ("red".equals(b.getQualifierValue())) {
				count++;
				Assert.assertEquals(NAMED.class, b.getQualifierAnno());
			}
			if ("tan".equals(b.getQualifierValue())) {
				count++;
				Assert.assertEquals(NAMED.class, b.getQualifierAnno());
			}
		}
		Assert.assertEquals(4, count);
	}

	public static class QualifierBeanTests_____________________ {
	}

	public static interface Egg {
	}

	@COMPONENT
	public static class BirdEgg implements Egg { // the only implements of Egg
	}

	@ColorAny(Color.TAN)
	public static class Bean2 {
		@INJECT
		Egg egg;

		@INJECT
		@NAMED("red")
		Leather l1;
 
		@INJECT
		@ColorRed
		Leather l2;

		@INJECT
		@ColorAny(Color.TAN)
		Leather l3;

		// @INJECT(required = false)
		// @NAMED("tan")
		// Leather l4;
		//
		// @INJECT(required = false)
		// Leather l5;
	}

	@Test
	public void testBean2() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		Bean2 bean = JBEANBOX.getBean(Bean2.class);
		Assert.assertTrue(bean.egg.getClass() == BirdEgg.class);
		
		Assert.assertTrue(bean.l1.getClass() == LeatherRed1.class);
		Assert.assertTrue(bean.l2.getClass() == LeatherRed2.class);
		Assert.assertTrue(bean.l3.getClass() == LeatherTan.class);
	}
}
