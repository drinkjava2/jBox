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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
import com.github.drinkjava2.jbeanbox.annotation.INJECT;
import com.github.drinkjava2.jbeanbox.annotation.NAMED;
import com.github.drinkjava2.jbeanbox.annotation.VALUE;

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

	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ColorRed {
		Color color() default Color.RED;
	}

	@Qualifier
	@Retention(RUNTIME)
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

	protected void Constr1ParamQualifierAnnoTests1_____________________() {
	}

	public static class ConstrBean1 {
		Leather purple;

		@INJECT(PurpleBox.class)
		public ConstrBean1(Leather purple) {// the constructor injection
			this.purple = purple;
		}
	}

	@Test
	public void testConstrBean1() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		ConstrBean1 bean = JBEANBOX.getBean(ConstrBean1.class);
		Assert.assertEquals("LeatherPurple", "" + bean.purple.getClass().getSimpleName());
	}

	protected void Constr1ParamQualifierAnnoTests2_____________________() {
	}

	public static class ConstrBean2 {
		Egg egg;

		@INJECT
		public ConstrBean2(Egg egg) {// the constructor injection
			this.egg = egg;
		}
	}

	@Test
	public void testConstrBean2() {
		JBEANBOX.bind(Egg.class, OnlyEgg.class);
		ConstrBean2 bean = JBEANBOX.getBean(ConstrBean2.class);
		Assert.assertEquals("OnlyEgg", "" + bean.egg.getClass().getSimpleName());
	}

	protected void Constr1ParamQualifierAnnoTests3_____________________() {
	}

	public static class ConstrBean3 {
		Leather purple;

		@INJECT
		public ConstrBean3(@NAMED("leatherPurple") Leather purple) {// the constructor injection
			this.purple = purple;
		}
	}

	@Test
	public void testConstrBean3() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		ConstrBean3 bean = JBEANBOX.getBean(ConstrBean3.class);
		Assert.assertEquals("LeatherPurple", "" + bean.purple.getClass().getSimpleName());
	}

	protected void Constr1ParamQualifierAnnoTests4_____________________() {
	}

	public static class ConstrBean4 {
		Leather purple;

		@INJECT
		@NAMED("LeatherPurple")
		public ConstrBean4(Leather purple) {// the constructor injection
			this.purple = purple;
		}
	}

	@Test
	public void testConstrBean4() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		ConstrBean4 bean = JBEANBOX.getBean(ConstrBean4.class);
		Assert.assertEquals("LeatherPurple", "" + bean.purple.getClass().getSimpleName());
	}

	protected void MethodQualifierAnnoTests_____________________() {
	}

	public static class MethodBean {
		Egg egg;
		Leather red;
		Leather green;
		Leather blue;
		Leather yellow;
		Leather purple1;
		Leather purple2;
		Leather purple3;
		int i;
		String s;

		@INJECT
		public void theMethod( // the constructor injection
				Egg egg,

				@ColorRed Leather red,

				@ColorAny(Color.GREEN) Leather green,

				@NAMED("blue") Leather blue,

				@NAMED("yellow") Leather yellow,

				@INJECT(LeatherPurple.class) Leather purple1,

				@NAMED("leatherPurple") Leather purple2,

				@INJECT(PurpleBox.class) Leather purple3,

				@VALUE("1") int i,

				@VALUE("2") String s

		) {
			this.egg = egg;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.yellow = yellow;
			this.purple1 = purple1;
			this.purple2 = purple2;
			this.purple3 = purple3;
			this.i = i;
			this.s = s;
		}
	}

	@Test
	public void testMethodBean() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		MethodBean bean = JBEANBOX.getBean(MethodBean.class);
		Assert.assertEquals("OnlyEgg", "" + bean.egg.getClass().getSimpleName());
		Assert.assertEquals("LeatherRed", "" + bean.red.getClass().getSimpleName());
		Assert.assertEquals("LeatherGreen", "" + bean.green.getClass().getSimpleName());
		Assert.assertEquals("LeatherBlue", "" + bean.blue.getClass().getSimpleName());
		Assert.assertEquals("LeatherYellow", "" + bean.yellow.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple1.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple2.getClass().getSimpleName());
		Assert.assertEquals("LeatherPurple", "" + bean.purple3.getClass().getSimpleName());
		Assert.assertEquals(1, bean.i);
		Assert.assertEquals("2", bean.s);
	}

	protected void MethodParamQualifierAnnoTests1_____________________() {
	}

	public static class MethodBean1 {
		Leather purple;

		@INJECT(PurpleBox.class)
		public void theMethod(Leather purple) {// the constructor injection
			this.purple = purple;
		}
	}

	@Test
	public void testMethodBean1() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		MethodBean1 bean = JBEANBOX.getBean(MethodBean1.class);
		Assert.assertEquals("LeatherPurple", "" + bean.purple.getClass().getSimpleName());
	}

	protected void MethodParamQualifierAnnoTests2_____________________() {
	}

	public static class MethodBean2 {
		Egg egg;

		@INJECT
		public void theMethod(Egg egg) {// the constructor injection
			this.egg = egg;
		}
	}

	@Test
	public void testMethodBean2() {
		JBEANBOX.bind(Egg.class, OnlyEgg.class);
		MethodBean2 bean = JBEANBOX.getBean(MethodBean2.class);
		Assert.assertEquals("OnlyEgg", "" + bean.egg.getClass().getSimpleName());
	}

	protected void MethodParamQualifierAnnoTests3_____________________() {
	}

	public static class MethodBean3 {
		Leather purple;

		@INJECT
		public void theMethod(@NAMED("leatherPurple") Leather purple) {// the constructor injection
			this.purple = purple;
		}
	}

	@Test
	public void testMethodBean3() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		MethodBean3 bean = JBEANBOX.getBean(MethodBean3.class);
		Assert.assertEquals("LeatherPurple", "" + bean.purple.getClass().getSimpleName());
	}

	protected void MethodParamQualifierAnnoTests4_____________________() {
	}

	public static class MethodBean4 {
		Leather purple;

		@INJECT
		@NAMED("LeatherPurple")
		public void theMethod(Leather purple) {// the constructor injection
			this.purple = purple;
		}
	}

	@Test
	public void testMethodBean4() {
		JBEANBOX.scanComponents(QualiferTest.class.getPackage().getName());
		MethodBean4 bean = JBEANBOX.getBean(MethodBean4.class);
		Assert.assertEquals("LeatherPurple", "" + bean.purple.getClass().getSimpleName());
	}

}
