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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.drinkjava2.jbeanbox.annotation.INJECT;
import com.github.drinkjava2.jbeanbox.annotation.CONS;
import com.github.drinkjava2.jbeanbox.annotation.POSTCONSTRUCT;
import com.github.drinkjava2.jbeanbox.annotation.PREDESTROY;
import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;

/**
 * BeanBoxContextTest
 * 
 * @author Yong Zhu
 * @since 2.4.7
 *
 */
public class AnnotationInjectTest {

	@Before
	public void init() {
		BeanBoxContext.reset();
	}

	protected void singletonTest_____________________() {
	}

	public static class Single {
	}

	public static class SingleBox1 extends BeanBox {
		{
			setBeanClass(Single.class);
		}
	}

	public static class SingleBox2 extends BeanBox {
		Object create() {
			return new Single();
		}
	}

	@Test
	public void singletonTest() {
		Assert.assertTrue(JBEANBOX.getBean(Single.class) == JBEANBOX.getBean(Single.class));
		Assert.assertTrue(JBEANBOX.getBean(SingleBox1.class) == JBEANBOX.getBean(SingleBox1.class));
		Assert.assertTrue(JBEANBOX.getBean(SingleBox2.class) == JBEANBOX.getBean(SingleBox2.class));
		JBEANBOX.bind("s1", Single.class);
		Assert.assertTrue(JBEANBOX.getBean("s1") == JBEANBOX.getBean(Single.class));
	}

	@PROTOTYPE
	public static class Pro {
	}
  

	public static class Pro2 extends BeanBox {
		{	beanClass=Pro.class;
			singleton = false;
		}
	}

	@Test
	public void prototypeTest() {
		Assert.assertTrue(JBEANBOX.getBean(Pro.class) != JBEANBOX.getBean(Pro.class)); 
		Assert.assertTrue(JBEANBOX.getBean(Pro2.class) != JBEANBOX.getBean(Pro2.class));
	}

	protected void BindTest_____________________() {
	}

	//@formatter:off
	public static class HelloBox extends BeanBox {{this.setAsConstant("Hello"); }} 
	//@formatter:on

	@Test
	public void getBean() {
		// test pure value
		Assert.assertEquals("Hello", new BeanBox().setAsConstant("Hello").getBean());

		JBEANBOX.bind("A", new BeanBox().setAsConstant("Hello"));
		Assert.assertEquals("Hello", JBEANBOX.getBean("A"));

		// bind pure value
		JBEANBOX.bind("D", "C").bind("C", "B").bind("B", "A");
		Assert.assertEquals("Hello", JBEANBOX.getBean("D"));

		BeanBox box1 = new BeanBox().setAsConstant("Hello");
		BeanBox box2 = new BeanBox().setTarget(box1);
		Assert.assertEquals("Hello", box2.getBean());
	}

	@Test
	public void getBeanByTarget1() { // Test target
		Assert.assertEquals("Hello", new BeanBox().setTarget(HelloBox.class).getBean());
		BeanBox a = new BeanBox().setTarget(new HelloBox());
		Assert.assertEquals("Hello", a.getBean());

		BeanBox b = new BeanBox().setTarget(a);
		JBEANBOX.bind("C", "B").bind("B", b);
		Assert.assertEquals("Hello", new BeanBox().setTarget("C").getBean());
	}

	@Test(expected = BeanBoxException.class)
	public void getBeanByTarget2() { // Test target not found
		JBEANBOX.getBean(new BeanBox().setTarget("AAA"));
	}

	@Test(expected = BeanBoxException.class)
	public void getBeanByTarget3() { // Test target is String.class
		JBEANBOX.getBean(new BeanBox().setTarget(String.class));
	}

	@Test
	public void bindTest() {
		// Test
		Assert.assertEquals("Hello", JBEANBOX.getBean(HelloBox.class));
		Assert.assertEquals("Hello", JBEANBOX.getBean(new HelloBox()));

		BeanBox a = new HelloBox();
		JBEANBOX.bind("A", a);
		Assert.assertEquals("Hello", JBEANBOX.getBean("A"));

		JBEANBOX.bind("C", "B").bind("B", "A");
		Assert.assertEquals("Hello", JBEANBOX.getBean("C"));

		Object o = new BeanBox().setTarget(new BeanBox().setTarget(a)).getBean();
		Assert.assertEquals("Hello", o);
	}

	public static class Foo {
	}

	public static class Bar extends Foo {
	}

	@Test
	public void getBean4() {
		Assert.assertNotEquals(JBEANBOX.getBean(Foo.class), JBEANBOX.getBean(Bar.class));

		BeanBoxContext.reset();
		BeanBox box = JBEANBOX.getBox(Foo.class);
		box.setTarget(Bar.class);
		Assert.assertEquals(JBEANBOX.getBean(box), JBEANBOX.getBean(Bar.class));

		BeanBoxContext.reset();
		JBEANBOX.bind(Foo.class, Bar.class);
		Assert.assertEquals(JBEANBOX.getBean(Foo.class), JBEANBOX.getBean(Bar.class));
	}

	protected void ClassInject_____________________() {
	}

	//@formatter:off
	@PROTOTYPE
	@CONS("3")
	public static class Demo4 { }
	
	@INJECT(Demo4.class)
	@PROTOTYPE
	public static class Demo5 { } 
	 
	@INJECT(value=Demo4.class )
	public static class Demo6 { } 
	
	@INJECT(value=Demo4.class  )
	public static interface inf1{}
	
	@INJECT(value=Demo4.class,  constant=true)
	public static interface inf2{}
	//@formatter:on

	@Test
	public void classInjectTest() {
		BeanBoxContext.reset();
		Object bean = JBEANBOX.getBean(Demo4.class);
		Assert.assertTrue(JBEANBOX.getBean(Demo4.class) == JBEANBOX.getBean(Demo4.class));
		Assert.assertEquals("3", bean);

		bean = JBEANBOX.getBean(Demo5.class);
		Assert.assertEquals("3", bean);

		bean = JBEANBOX.getBean(Demo6.class);
		Assert.assertEquals("3", bean);

		bean = JBEANBOX.getBean(inf1.class);
		Assert.assertEquals("3", bean);

		bean = JBEANBOX.getBean(inf2.class);
		Assert.assertEquals(Demo4.class, bean);
	}

	protected void ConstructInject___________() {
	}

	//@formatter:off
	public static class CA {}
	public static class CB {}
	public static class C1 { int i = 0; @INJECT public C1() { i = 2; } } 
	public static class C2 { int i = 0; @INJECT public C2(@CONS("2") int a) { i = a; } }
	public static class C3 { int i = 0; @CONS("2") public C3(int a) { i = a; } }
	public static class C4 { int i = 0; @INJECT public C4(@CONS("2") Integer a,@CONS("2") byte b ) { i = b; } }
	public static class C5 { Object o ; @INJECT(value=Bar.class, constant=true) public C5(Object a) { o = a; } }
	public static class C6 { Object o1,o2 ; @INJECT public C6(CA a, CB b) { o1 = a; o2=b; } }
	//@formatter:on

	@Test
	public void ConstructInjectTest() {
		C1 bean = JBEANBOX.getInstance(C1.class);
		Assert.assertEquals(2, bean.i);

		C2 bean2 = JBEANBOX.getInstance(C2.class);
		Assert.assertEquals(2, bean2.i);

		C3 bean3 = JBEANBOX.getInstance(C3.class);
		Assert.assertEquals(2, bean3.i);

		C4 bean4 = JBEANBOX.getInstance(C4.class);
		Assert.assertEquals(2, bean4.i);

		C5 bean5 = JBEANBOX.getInstance(C5.class);
		Assert.assertEquals(Bar.class, bean5.o);

		C6 bean6 = JBEANBOX.getInstance(C6.class);
		Assert.assertEquals(CA.class, bean6.o1.getClass());
		Assert.assertEquals(CB.class, bean6.o2.getClass());
	}

	protected void PostConstructPreDestory___________() {
	}

	public static class PostConsAndPreDestBean1 {
		static int count = 0;

		@POSTCONSTRUCT
		public void postcons1() {
			count++;
		}

		@PREDESTROY
		public void predest1() {
			count++;
		}
	}

	public static class PostConsAndPreDestBean2 {
		static int count = 0;

		@PostConstruct
		public void postcons2() {
			count++;
		}

		@PreDestroy
		public void predest2() {
			count++;
		}
	}

	@Test
	public void postConsAndPreDestTest() {
		JBEANBOX.getInstance(PostConsAndPreDestBean1.class);
		JBEANBOX.reset();
		Assert.assertEquals(2, PostConsAndPreDestBean1.count);

		JBEANBOX.getInstance(PostConsAndPreDestBean2.class);
		JBEANBOX.reset();
		Assert.assertEquals(2, PostConsAndPreDestBean2.count);
	}

	protected void FieldInject_______________() {
	}

	public static class ClassA {
		int i = 1;
	}

	public static class FieldInject2 {
		@INJECT(required = false)
		public String field0 = "aa";

		@INJECT(value = ClassA.class, constant = false, required = true)
		private ClassA field1;

		@INJECT(value = ClassA.class, constant = false, required = false)
		private ClassA field2;

		@INJECT(HelloBox.class)
		private String field3;

		@CONS(value = "true")
		private Boolean field4;

		@CONS("5")
		private long field5;

		@CONS("6")
		private Long field6;

		@Autowired(required = false)
		public String field7 = "7";

		@Inject
		public CA ca;

		@Autowired
		public CB cb;
	}

	@Test
	public void fieldInjectTest1() {
		FieldInject2 bean = JBEANBOX.getInstance(FieldInject2.class);
		Assert.assertEquals("aa", bean.field0);
		Assert.assertEquals(1, bean.field1.i);
		Assert.assertEquals(1, bean.field2.i);
		Assert.assertEquals("Hello", bean.field3);
		Assert.assertEquals(true, bean.field4);
		Assert.assertEquals(5, bean.field5);
		Assert.assertEquals(6, (long) bean.field6);
		Assert.assertEquals("7", bean.field7);
		Assert.assertEquals(CA.class, bean.ca.getClass());
		Assert.assertEquals(CB.class, bean.cb.getClass());
	}

	public static class FieldInject3 {
		@Autowired(required = true)
		public String field1;

		@Inject
		public String field2;
	}

	@Test(expected = BeanBoxException.class)
	public void fieldInjectTest12() {
		JBEANBOX.getInstance(FieldInject3.class);
	}

	protected void MethodInject_______________() {
	}

	public static class MethodInject1 {
		public String s1;
		public String s2;
		public long l3;
		public Boolean bl4;
		public String s5;
		public byte bt5;
		public CA a;

		@INJECT(HelloBox.class)
		private void method1(String a) {
			s1 = a;
		}

		@INJECT
		private void method2(@INJECT(value = HelloBox.class) String a) {
			s2 = a;
		}

		@INJECT
		private void method3(@CONS("3") long a) {
			l3 = a;
		}

		@CONS("true")
		private void method4(boolean a) {
			bl4 = a;
		}

		@INJECT
		private void method5(@INJECT(HelloBox.class) String a, @CONS("5") Byte b) {
			s5 = a;
			bt5 = b;
		}

		@INJECT
		private void method6(CA a) {
			this.a = a;
		}
	}

	@Test
	public void methodInjectTest() {
		MethodInject1 bean = JBEANBOX.getBean(MethodInject1.class);
		Assert.assertEquals("Hello", bean.s1);
		Assert.assertEquals("Hello", bean.s2);
		Assert.assertEquals(3, bean.l3);
		Assert.assertEquals(true, bean.bl4);
		Assert.assertEquals("Hello", bean.s5);
		Assert.assertEquals(5, bean.bt5);
		Assert.assertEquals(CA.class, bean.a.getClass());
	}

	 

}
