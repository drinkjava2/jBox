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

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
@SuppressWarnings("all")
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

	@Controller("a4")
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
		Assert.assertEquals(A4.class, JBEANBOX.getObject("a4").getClass());
	}

	protected void fieldQualifierAnnoTests_____________________() {
	}

	public enum Color {
		BLACK, RED, GREEN, BLUE, YELLOW
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER })
	@Qualifier
	public @interface ColorRed {
		Color color() default Color.RED;
	}

	@Retention(RUNTIME)
	@Target({ FIELD, TYPE, CONSTRUCTOR, METHOD, PARAMETER })
	@Qualifier
	public @interface ColorAny {
		Color value() default Color.BLACK;
	}

	public static interface Leather {
	}

	@COMPONENT
	@ColorRed
	public static class LeatherRed implements Leather {
	}

	@COMPONENT
	@ColorAny(Color.GREEN)
	public static class LeatherGreen implements Leather {
	}

	@COMPONENT
	@NAMED("blue")
	public static class LeatherBlue implements Leather {
	}

	@COMPONENT("yellow")
	public static class LeatherYellow implements Leather {
	}

	@COMPONENT
	public static class LeatherPurple implements Leather {
	}

	public static class PurpleBox extends BeanBox {
		{
			beanClass = LeatherPurple.class;
		}
	}

	public static class PurpleBox2 extends BeanBox {
		{
			this.setTarget("leatherPurple");
		}
	}

	public static class Bean {
		@INJECT
		@ColorRed
		Leather red;

//		@INJECT
//		@ColorAny(Color.GREEN)
//		Leather green;
//
//		@INJECT
//		@NAMED("blue")
//		Leather blue;
//
//		@INJECT
//		@NAMED("yellow")
//		Leather yellow;
//
//		@INJECT(LeatherPurple.class)
//		Leather purple1;
//
//		@INJECT
//		@NAMED("leatherPurple")
//		Leather purple2;
//
//		@INJECT
//		@NAMED("LeatherPurple")
//		Leather purple3;
//
//		@INJECT(PurpleBox.class)
//		Leather purple4;

//		@INJECT(required = false)
//		@NAMED("notExist")
//		Leather notExist;
	}

	@Test
	public void testBean() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		Bean bean = JBEANBOX.getBean(Bean.class);
//		Assert.assertEquals("LeatherRed", "" + bean.red.getClass().getSimpleName());
//		Assert.assertEquals("LeatherGreen", "" + bean.green.getClass().getSimpleName());
//		Assert.assertEquals("LeatherBlue", "" + bean.blue.getClass().getSimpleName());
//		Assert.assertEquals("LeatherYellow", "" + bean.yellow.getClass().getSimpleName());
//		Assert.assertEquals("LeatherPurple", "" + bean.purple1.getClass().getSimpleName());
//		Assert.assertEquals("LeatherPurple", "" + bean.purple2.getClass().getSimpleName());
//		Assert.assertEquals("LeatherPurple", "" + bean.purple3.getClass().getSimpleName());
//		Assert.assertEquals("LeatherPurple", "" + bean.purple4.getClass().getSimpleName());
		//Assert.assertEquals(null, bean.notExist);
	}

	protected void DuplicateComponetsTest_____________________() {
	}

	public static class Bean2 {
		@INJECT
		Leather other; // error! becuase more than 1 Leather components
	}

	@Test(expected = BeanBoxException.class)
	public void testDuplicateComponentsError() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		JBEANBOX.getBean(Bean2.class);
	}

	protected void ConstrParamQualifierAnnoTests_____________________() {
	}

	public static class ConstrBean {
		Leather l1;
		Leather l2;
		Leather l3;
		Leather l4;
		Leather l5;
		Leather l6;
		Leather l7;

		@INJECT
		public ConstrBean( // the constructor injection
				@ColorRed Leather l1

//				@ColorAny(Color.GREEN) Leather l2,
//
//				@NAMED("blue") Leather l3,
//
//				@NAMED("yellow") Leather l4,
//
//				@INJECT(LeatherPurple.class) Leather l5,
//
//				@NAMED("leatherPurple") Leather l6,
//
//				@NAMED("LeatherPurple") Leather l7,
//
//				@INJECT(PurpleBox.class) Leather purple4

		) {
			this.l1 = l1;
			this.l2 = l2;
			this.l3 = l3;
			this.l4 = l4;
			this.l5 = l5;
			this.l6 = l6;
			this.l7 = l7;
		}
	}

	@Test
	public void testConstrBean() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		ConstrBean bean = JBEANBOX.getBean(ConstrBean.class);
	}

}
