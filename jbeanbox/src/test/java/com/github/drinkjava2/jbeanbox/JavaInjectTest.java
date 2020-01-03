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

import static com.github.drinkjava2.jbeanbox.JBEANBOX.autowired;
import static com.github.drinkjava2.jbeanbox.JBEANBOX.inject;
import static com.github.drinkjava2.jbeanbox.JBEANBOX.value;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.AnnotationInjectTest.Single;
import com.github.drinkjava2.jbeanbox.AnnotationInjectTest.SingleBox1;
import com.github.drinkjava2.jbeanbox.AnnotationInjectTest.SingleBox2;

/**
 * Java Inject Test
 * 
 * @author Yong Zhu
 * @since 2.4.7
 *
 */
@SuppressWarnings("unused")
public class JavaInjectTest {

	@Before
	public void init() {
		BeanBoxContext.reset();
	}

	public static class Foo {
		int i = 1;
	}

	public static class Bar extends Foo {
	}

	public static class HelloBox extends BeanBox {
		{
			this.setAsValue("Hello");
		}
	}

	protected void BasicTest__________________() {
	}

	@Test
	public void getBean() {
		// test pure value
		Assert.assertEquals("Hello", new BeanBox().setAsValue("Hello").getBean());

		JBEANBOX.bind("A", new BeanBox().setAsValue("Hello"));
		Assert.assertEquals("Hello", JBEANBOX.getBean("A"));

		// bind pure value
		JBEANBOX.bind("D", "C").bind("C", "B").bind("B", "A");
		Assert.assertEquals("Hello", JBEANBOX.getBean("D"));

		BeanBox box1 = new BeanBox().setAsValue("Hello");
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
	public void getBean4() {
		Assert.assertNotEquals(JBEANBOX.getBean(Foo.class), JBEANBOX.getBean(Bar.class));

		BeanBoxContext.reset();
		BeanBox box = JBEANBOX.getBeanBox(Foo.class);
		box.setTarget(Bar.class);
		Bar b1 = JBEANBOX.getBean(box);
		Bar b2 = JBEANBOX.getBean(Bar.class);
		Assert.assertEquals(b1, b2);

		BeanBoxContext.reset();
		JBEANBOX.bind(Foo.class, Bar.class);
		Foo f = JBEANBOX.getBean(box);
		Bar b = JBEANBOX.getBean(Bar.class);
		Assert.assertEquals(f, b);
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

	@Test
	public void singletonTest() {
		Assert.assertTrue(JBEANBOX.getBean(Single.class) == JBEANBOX.getBean(Single.class));
		Assert.assertTrue(JBEANBOX.getBean(SingleBox1.class) == JBEANBOX.getBean(SingleBox1.class));
		Assert.assertTrue(JBEANBOX.getBean(SingleBox2.class) == JBEANBOX.getBean(SingleBox2.class));
		JBEANBOX.bind("s1", Single.class);
		Assert.assertTrue(JBEANBOX.getBean("s1") == JBEANBOX.getBean(Single.class));
	}

	protected void ClassInject_____________________() {
	}

	public static class ConstBox extends BeanBox {
		{
			this.setTarget("Foo").setPureValue(true);
		}
	}

	@Test
	public void classInjectTest1() {
		Assert.assertEquals("Foo", JBEANBOX.getBean(ConstBox.class));
	}

	public static class FooBox extends BeanBox {
		{
			this.setBeanClass(Foo.class);
		}
	}

	public static class Foo2Box extends BeanBox {
		{
			this.setTarget(Foo.class);
		}
	}

	public static <T> T getIns(Class<Class<T>> a) {
		return null;
	}

	@Test
	public void classInjectTest2() {
		Assert.assertEquals(1, ((Foo) JBEANBOX.getBean(Foo2Box.class)).i);
		Assert.assertEquals(1, ((Foo) JBEANBOX.getBean(Foo2Box.class)).i);
	}

	protected void ConstructInject___________() {
	}

	//@formatter:off
	public static class CA {}
	public static class CB {}
	public static class C1 { int i = 0;   public C1() { i = 2; } } 
	public static class C2 { int i = 0;   public C2(  Integer a) { i = a; } } 
	public static class C4 { int i = 0;  public C4( Integer a,  byte b ) { i = b; } }
	public static class C5 { Object o;   public C5(Object a) { o = a; } }
	public static class C6 { Object o1, o2 ;  public C6(CA a, CB b) { o1 = a; o2=b; } }
	//@formatter:on

	@Test
	public void ConstructInjectTest() {
		C1 bean = JBEANBOX.getInstance(C1.class);
		Assert.assertEquals(2, bean.i);

		BeanBox box = new BeanBox().injectConstr(C2.class, 2);
		C2 bean2 = JBEANBOX.getBean(box);
		Assert.assertEquals(2, bean2.i);

		box = new BeanBox().injectConstruct(C4.class, Integer.class, byte.class, "2", "2");
		C4 bean4 = JBEANBOX.getBean(box);
		Assert.assertEquals(2, bean4.i);

		box = new BeanBox().injectConstruct(C5.class, Object.class, value(Bar.class));
		C5 bean5 = JBEANBOX.getBean(box);
		Assert.assertEquals(Bar.class, bean5.o);

		box = new BeanBox().injectConstruct(C6.class, CA.class, CB.class, inject(), inject());
		C6 bean6 = JBEANBOX.getBean(box);
		Assert.assertEquals(CA.class, bean6.o1.getClass());
		Assert.assertEquals(CB.class, bean6.o2.getClass());
	}

	protected void PostConstructPreDestory___________() {
	}

	public static class P {
		int count = 0;

		public void postcons1() {
			count++;
		}

		public void predest1() {
			count++;
		}
	}

	@Test
	public void postConsAndPreDestTest2() {
		BeanBox box = new BeanBox().setBeanClass(P.class).setPostConstruct("postcons1").setSingleton(true)
				.setPreDestroy("predest1");
		P p = JBEANBOX.getBean(box);
		JBEANBOX.reset();
		Assert.assertEquals(2, p.count);
		Object o1 = JBEANBOX.getBean(box);
		Object o2 = JBEANBOX.getBean(box);
		Assert.assertEquals(o1, o2);
	}

	protected void FieldInject_______________() {
	}

	public static class ClassA {
		int i = 1;
	}

	public static class FieldInject2 {
		public String field0 = "aa";
		private ClassA field1;
		private ClassA field2;
		private String field3;
		private Boolean field4;
		private long field5;
		private Long field6;
		public String field7 = "7";
		public CA field8;
		public CB field9;
	}

	@Test
	public void fieldInjectTest1() {
		BeanBox box = new BeanBox().setBeanClass(FieldInject2.class);
		box.injectField("field0", inject(false, false, false));
		box.injectField("field1", inject(ClassA.class));
		box.injectField("field2", ClassA.class);
		box.injectField("field3", HelloBox.class);
		box.injectValue("field4", true);
		box.injectValue("field5", 5L);
		box.injectField("field6", value("6"));
		box.injectField("field7", inject(EMPTY.class, false, false));
		box.injectField("field8", inject());
		box.injectField("field9", autowired());

		FieldInject2 bean = JBEANBOX.getBean(box);
		Assert.assertEquals("aa", bean.field0);
		Assert.assertEquals(1, bean.field1.i);
		Assert.assertEquals(1, bean.field2.i);
		Assert.assertEquals("Hello", bean.field3);
		Assert.assertEquals(true, bean.field4);
		Assert.assertEquals(5, bean.field5);
		Assert.assertEquals(6, (long) bean.field6);
		Assert.assertEquals("7", bean.field7);
		Assert.assertEquals(CA.class, bean.field8.getClass());
		Assert.assertEquals(CB.class, bean.field9.getClass());
	}

	protected void MethodInject_______________() {
	}

	public static class MethodInject1 {
		public String s1;
		public long l3;
		public Boolean bl4;
		public String s5;
		public byte bt5;
		public CA a;

		private void method1(String a) {
			s1 = a;
		}

		private void method3(long a) {
			l3 = a;
		}

		private void method4(boolean a) {
			bl4 = a;
		}

		private void method5(String a, Byte b) {
			s5 = a;
			bt5 = b;
		}

		private void method6(CA a) {
			this.a = a;
		}
	}

	@Test
	public void methodInjectTest() {
		BeanBox box = new BeanBox().setBeanClass(MethodInject1.class);
		box.injectMethod("method1", String.class, HelloBox.class);
		box.injectMethod("method3", long.class, "3");
		box.injectMethod("method4", boolean.class, "true");
		box.injectMethod("method5", String.class, Byte.class, inject(HelloBox.class), value("5"));
		box.injectMethod("method6", CA.class, autowired());

		MethodInject1 bean = JBEANBOX.getBean(box);
		Assert.assertEquals("Hello", bean.s1);
		Assert.assertEquals(3, bean.l3);
		Assert.assertEquals(true, bean.bl4);
		Assert.assertEquals("Hello", bean.s5);
		Assert.assertEquals(5, bean.bt5);
		Assert.assertEquals(CA.class, bean.a.getClass());
	}

	// ===============create and config method name ===========
	protected void CreateConfigMethod1_______________() {
	}

	public static class CFdemo1 {
		String a;
		String b;
	}

	public static class CFdemoBox extends BeanBox {

		public Object create(BeanBoxContext b, Set<Object> history) {
			CFdemo1 c = new CFdemo1();
			c.a = "1";
			return c;
		}

		public void config(Object c) {
			((CFdemo1) c).b = "2";
		}
	}

	public static class CFdemo2 {
		CFdemo1 field1;
		CFdemo1 field2;
	}

	public static class CFdemoBox2 extends BeanBox {

		public Object create(BeanBoxContext ctx, Set<Object> history) {
			CFdemo2 c2 = new CFdemo2();
			c2.field1 = ctx.getBean(CFdemoBox.class);
			return c2;
		}

		public void config(Object c, BeanBoxContext ctx, Set<Object> history) {
			((CFdemo2) c).field2 = ctx.getBean(CFdemoBox.class);
		}
	}

	@Test
	public void createAndConfigMethodTest1() {
		CFdemo1 c1 = JBEANBOX.getBean(CFdemoBox.class);
		Assert.assertEquals("1", c1.a);
		Assert.assertEquals("2", c1.b);

		CFdemo2 c2 = JBEANBOX.getBean(CFdemoBox2.class);
		Assert.assertEquals("1", c2.field1.a);
		Assert.assertEquals("2", c2.field2.b);
		Assert.assertEquals(c2.field1, c2.field2);
	}

	protected void CreateConfigMethod2_______________() {
	}

	public static class CFdemo3 {
		String a;
		String b;
		String c;
	}

	public static class CFdemo3Box extends BeanBox {
		{
			this.setBeanClass(CFdemo3.class);
			injectValue("c", "3");
		}

		public Object create() {
			CFdemo3 c = new CFdemo3();
			c.a = "1";
			return c;
		}

		public void config(Object c) {
			((CFdemo3) c).b = "2";
		}

	}

	@Test
	public void createAndConfigMethodTest2() {
		CFdemo3 c3 = JBEANBOX.getBean(CFdemo3Box.class);
		Assert.assertEquals("1", c3.a);
		Assert.assertEquals("2", c3.b);
		Assert.assertEquals("3", c3.c);
	}

}
