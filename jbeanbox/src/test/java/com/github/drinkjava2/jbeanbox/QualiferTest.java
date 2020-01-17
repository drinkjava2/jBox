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
@SuppressWarnings("all") // Yong
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

	public static interface Egg {
	}

	@COMPONENT
	public static class OnlyEgg implements Egg {
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
		Egg egg;

		@INJECT
		@ColorRed
		Leather red;

		@INJECT
		@ColorAny(Color.GREEN)
		Leather green;

		@INJECT
		@NAMED("blue")
		Leather blue;

		@INJECT
		@NAMED("yellow")
		Leather yellow;

		@INJECT(LeatherPurple.class)
		Leather purple1;

		@INJECT
		@NAMED("leatherPurple")
		Leather purple2;

		@INJECT(PurpleBox.class)
		Leather purple3;

		@INJECT(required = false)
		@NAMED("notExist")
		Leather notExist;
	}

	@Test
	public void testBean() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		Bean bean = JBEANBOX.getBean(Bean.class);
		Assert.assertEquals("OnlyEgg", "" + bean.egg.getClass().getSimpleName());
		Assert.assertEquals("LeatherRed", "" + bean.red.getClass().getSimpleName());
		Assert.assertEquals("LeatherGreen", "" + bean.green.getClass().getSimpleName());
		Assert.assertEquals("LeatherBlue", "" + bean.blue.getClass().getSimpleName());
		Assert.assertEquals("LeatherYellow", "" + bean.yellow.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple1.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple2.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple3.getClass().getSimpleName());
		// Assert.assertEquals(null, bean.notExist);
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
		Egg egg;
		Leather red;
		Leather green;
		Leather blue;
		Leather yellow;
		Leather purple1;
		Leather purple2;
		Leather purple3;

		@INJECT
		public ConstrBean( // the constructor injection
				Egg egg,

				@ColorRed Leather red,

				@ColorAny(Color.GREEN) Leather green,

				@NAMED("blue") Leather blue,

				@NAMED("yellow") Leather yellow,

				@INJECT(LeatherPurple.class) Leather purple1,

				@NAMED("leatherPurple") Leather purple2,

				@INJECT(PurpleBox.class) Leather purple3

		) {
			this.egg = egg;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.yellow = yellow;
			this.purple1 = purple1;
			this.purple2 = purple2;
			this.purple3 = purple3;
		}
	}

	@Test
	public void testConstrBean() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		ConstrBean bean = JBEANBOX.getBean(ConstrBean.class);
		Assert.assertEquals("OnlyEgg", "" + bean.egg.getClass().getSimpleName());
		Assert.assertEquals("LeatherRed", "" + bean.red.getClass().getSimpleName());
		Assert.assertEquals("LeatherGreen", "" + bean.green.getClass().getSimpleName());
		Assert.assertEquals("LeatherBlue", "" + bean.blue.getClass().getSimpleName());
		Assert.assertEquals("LeatherYellow", "" + bean.yellow.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple1.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple2.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple3.getClass().getSimpleName());
	}

	protected void Constr1ParamQualifierAnnoTests_____________________() {
	}

	public static class ConstrBean1Param {
		Leather purple;

		@INJECT(PurpleBox.class)
		public ConstrBean1Param(Leather purple) {// the constructor injection
			this.purple = purple;
		}
	}

	@Test
	public void testConstrBean1Param() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		ConstrBean1Param bean = JBEANBOX.getBean(ConstrBean1Param.class);
		Assert.assertEquals("LeatherPurple", "" + bean.purple.getClass().getSimpleName());
	}
}
