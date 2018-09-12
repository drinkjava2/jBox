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
 * Unit test for BeanBoxUtils
 * 
 * @author Yong Zhu
 * @since 2.4.7
 */
@SuppressWarnings("unused")
public class BeanBoxUtilsTest {

	@Before
	public void init() {
		BeanBoxContext.reset();
	}

	public static BeanBox class2BeanBox(Class<?> clazz) {
		return BeanBoxUtils.getUniqueBeanBox(BeanBoxContext.globalBeanBoxContext, clazz);
	}

	protected void BindTest_____________________() {
	}

	//@formatter:off
	public static class Bind1 extends BeanBox {{ setAsConstant("Foo"); }} 
	public static class Bind2 extends BeanBox {{ setTarget(Bind1.class); }} 
	//@formatter:on

	@Test
	public void getBean() {
		BeanBox box = class2BeanBox(Bind2.class);
		Assert.assertEquals(Bind1.class, box.getTarget());
		Assert.assertEquals("Foo", JBEANBOX.getBean(Bind2.class));
	}

	protected void ClassInject_______________() {
	}

	//@formatter:off
	@PROTOTYPE
	@CONS("3")
	public static class Demo4 { }
	
	@INJECT(Demo4.class)
	@PROTOTYPE
	public static class Demo5 { } 
	 
	public static class Demo6 { } 
	
	@INJECT(Demo1.class)
	public static interface inf1{}
	
	//@formatter:on
	@Test
	public void classInjectTest() throws NoSuchMethodException, SecurityException {
		BeanBox box = class2BeanBox(Demo4.class);
		Assert.assertEquals(false, box.isSingleton());
		Assert.assertEquals(true, box.isConstant());
		Assert.assertEquals("3", box.getTarget());
		Assert.assertEquals(Demo4.class, box.getBeanClass());
		Assert.assertEquals(null, box.getSingletonId());

		box = class2BeanBox(Demo5.class);
		Assert.assertEquals(Demo4.class, box.getTarget());
		Assert.assertEquals(false, box.isConstant());
		Assert.assertEquals(false, box.isSingleton());
		Assert.assertEquals(Demo5.class, box.getBeanClass());
		Assert.assertEquals(null, box.getSingletonId());

		box = class2BeanBox(Demo6.class);
		Assert.assertEquals(null, box.getTarget());
		Assert.assertEquals(false, box.isConstant());
		Assert.assertEquals(true, box.isSingleton());
		Assert.assertEquals(Demo6.class, box.getBeanClass());
		Assert.assertEquals(box, box.getSingletonId());

		box = class2BeanBox(inf1.class);
		Assert.assertEquals(false, box.isConstant());
		Assert.assertEquals(true, box.isSingleton());
		Assert.assertEquals(Demo1.class, box.getTarget());
		Assert.assertEquals(inf1.class, box.getBeanClass());
		Assert.assertEquals(null, box.getSingletonId());
	}

	protected void ConstructInject_______________() {
	}

	public static class Constinject1 {
		@INJECT
		public Constinject1() {
		}
	}

	public static class Constinject2 {
		@CONS("ABC")
		public Constinject2(String a) {
		}
	}

	public static class Constinject3 {
		public Constinject3() {
		}

		@INJECT
		public Constinject3(@CONS("ABC") String a, @INJECT(Demo1.class) boolean b, @CONS("3") int c,
				@CONS("4") int d) {
		}
	}

	@Test
	public void constructorInjectTest() throws SecurityException, NoSuchMethodException {
		BeanBox box = class2BeanBox(Constinject1.class);
		Assert.assertEquals(Constinject1.class.getConstructor(), box.getConstructor());

		box = class2BeanBox(Constinject2.class);
		Assert.assertEquals(Constinject2.class.getConstructor(String.class), box.getConstructor());

		box = class2BeanBox(Constinject3.class);
		Assert.assertNotEquals(Constinject3.class.getConstructor(), box.getConstructor());

	}

	protected void PostConstructPreDestory_______________() {
	}

	public static class Demo1 {
	}

	public static class Demo2 {
		@POSTCONSTRUCT
		public void postcons1() {
		}

		@PREDESTROY
		public void predest1() {
		}

	}

	public static class Demo3 {
		@PostConstruct // JSR annotation
		public void postcons1() {
		}

		@PreDestroy // JSR annotation
		public void predest1() {
		}

	}

	@Test
	public void postConsAndPreDestTest() {
		BeanBox box = class2BeanBox(Demo1.class);
		Assert.assertNull(box.getPostConstruct());
		Assert.assertNull(box.getPreDestroy());

		box = class2BeanBox(Demo2.class);
		Assert.assertEquals("postcons1", box.getPostConstruct().getName());
		Assert.assertEquals("predest1", box.getPreDestroy().getName());

		box = class2BeanBox(Demo3.class);
		Assert.assertEquals("postcons1", box.getPostConstruct().getName());
		Assert.assertEquals("predest1", box.getPreDestroy().getName());
	}

	protected void FieldInject_______________() {
	}

	public static class Demo7 {
		@INJECT(Demo6.class)
		private String field1;

		@INJECT(value = Demo6.class, constant = false, required = false)
		private String field2;

		@CONS("false")
		private Boolean field3;

		@CONS("4")
		private Byte field4;

		@Autowired(required = false)
		private String field5;

		@Inject
		private String field6;

		private String field7;
	}

	@Test
	public void fieldInjectTest() {
		BeanBox box = class2BeanBox(Demo7.class);
		Assert.assertEquals(6, box.getFieldInjects().size());
	}

	protected void MethodInject_______________() {
	}

	public static class Demo9 {

		@INJECT(Demo1.class)
		private void method1(String a) {
		}

		@INJECT
		private void method2(@INJECT(value = Demo6.class, constant = false) String a) {
		}

		@INJECT
		private void method3(@INJECT(value = Demo6.class, constant = true, required = false) String a,
				@CONS("true") int b) {
		}

		@CONS("false")
		private void method4(boolean a) {
		}

		@INJECT
		private void method5(@Autowired(required = false) String a) {
		}

		private void method6() {
		}

	}

	@Test
	public void methodInjectTest() {
		BeanBox box = class2BeanBox(Demo9.class);
		Assert.assertEquals(5, box.getMethodInjects().size());
	}

	// ===============create and config method name ===========
	protected void CreateConfigMethod_______________() {
	}

	public static class CreateAndConfigMethod1 extends BeanBox {

		public Object create() {
			return null;
		}

		public void config(Object obj) {
		}
	}

	@Test
	public void createAndConfigMethodTest() throws SecurityException, NoSuchMethodException {
		BeanBox box = class2BeanBox(CreateAndConfigMethod1.class);
		Assert.assertEquals(CreateAndConfigMethod1.class.getDeclaredMethod("create"), box.getCreateMethod());
		Assert.assertEquals(CreateAndConfigMethod1.class.getDeclaredMethod("config", Object.class),
				box.getConfigMethod());
	}

}
