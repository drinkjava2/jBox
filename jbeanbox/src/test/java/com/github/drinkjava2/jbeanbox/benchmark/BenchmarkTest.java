package com.github.drinkjava2.jbeanbox.benchmark;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBoxContext;
import com.github.drinkjava2.jbeanbox.JBEANBOX;
import com.github.drinkjava2.jbeanbox.benchmark.objects.A;

/**
 * A simple benchmark test
 * 
 * @since 2.4
 */

public class BenchmarkTest {
	static long REPEAT_TIMES = 1;
	static boolean PRINT_TIMEUSED = false;

	@Test
	public void speedTest() throws Exception {
		try {
			PRINT_TIMEUSED = false;
			REPEAT_TIMES = 3;// warm up
			runTestMethods();
			PRINT_TIMEUSED = true;
			REPEAT_TIMES = 5000;// change to 5000000 to do real test
			System.out.println("Benchmark test for repeat " + REPEAT_TIMES + " times:");
			runTestMethods();
		} finally {
			PRINT_TIMEUSED = false;
			REPEAT_TIMES = 1;
		}
	}

	private void runTestMethods() throws Exception {
		runMethod("testAnnotationSingleton");
		runMethod("testAnnotationPrototype");
		runMethod("testConstructInjectSingleTon");
		runMethod("testConstructInjectPrototype");
		runMethod("testCreateMethodSingleton");
		runMethod("testCreateMethodPrototype");
	}

	public void runMethod(String methodName) throws Exception {
		Method m = this.getClass().getMethod(methodName);
		long start = System.currentTimeMillis();
		m.invoke(this);
		long end = System.currentTimeMillis();
		String timeused = "" + ((end - start) * 1000) / 1000000.0;
		if (PRINT_TIMEUSED)
			System.out.println(String.format("%35s: %6s s", methodName, timeused));
	}

	@Test
	public void testAnnotationSingleton() {
		BeanBoxContext.reset();
		JBEANBOX.getBeanBox(A.class).setSingleton(true);
		for (int i = 0; i < REPEAT_TIMES; i++)
			JBEANBOX.getBean(A.class);
	}

	@Test
	public void testAnnotationPrototype() {
		BeanBoxContext.reset();
		for (int i = 0; i < REPEAT_TIMES; i++)
			JBEANBOX.getPrototypeBean(A.class);

		JBEANBOX.reset();
		A a1 = JBEANBOX.getPrototypeBean(A.class);
		A a2 = JBEANBOX.getPrototypeBean(A.class);
		Assert.assertTrue(a1 != a2);
		Assert.assertTrue(a1.b.c.d1.e != a2.b.c.d1.e);
	}

	@Test
	public void testConstructInjectSingleTon() {
		BeanBoxContext.reset();
		BeanBoxContext ctx = new BeanBoxContext();
		ctx.setAllowAnnotation(false);
		ctx.getBeanBox(BoxConfig1.ABox.class).setSingleton(true);
		for (int i = 0; i < REPEAT_TIMES; i++)
			ctx.getBean(BoxConfig1.ABox.class);
	}

	@Test
	public void testConstructInjectPrototype() {
		BeanBoxContext.reset();
		BeanBoxContext ctx = new BeanBoxContext();
		ctx.setAllowAnnotation(false);
		ctx.getBeanBox(BoxConfig1.ABox.class).setPrototype(true);
		for (int i = 0; i < REPEAT_TIMES; i++)
			ctx.getBean(BoxConfig1.ABox.class);
	}

	public void testCreateMethodSingleton() {
		BeanBoxContext.reset();
		BeanBoxContext ctx = new BeanBoxContext();
		ctx.setAllowAnnotation(false);
		ctx.getBeanBox(BoxConfig2.ABox.class).setSingleton(true);
		for (int i = 0; i < REPEAT_TIMES; i++)
			ctx.getBean(BoxConfig2.ABox.class);
	}

	public void testCreateMethodPrototype() {
		BeanBoxContext.reset();
		BeanBoxContext ctx = new BeanBoxContext();
		ctx.setAllowAnnotation(false);
		ctx.getBeanBox(BoxConfig1.ABox.class).setPrototype(true);
		for (int i = 0; i < REPEAT_TIMES; i++)
			ctx.getBean(BoxConfig2.ABox.class);
	}

	@Test
	public void singletonTest() {
		JBEANBOX.reset();
		A a1 = JBEANBOX.getBean(A.class);
		A a2 = JBEANBOX.getBean(A.class);
		Assert.assertTrue(a1 != a2);
		Assert.assertTrue(a1.b.c.d1.e != a2.b.c.d1.e);

		for (int i = 0; i < REPEAT_TIMES; i++) {
			a2 = JBEANBOX.getBean(A.class);
		}

		JBEANBOX.reset();
		JBEANBOX.getBeanBox(A.class).setSingleton(true);
		a1 = JBEANBOX.getBean(A.class);
		a2 = JBEANBOX.getBean(A.class);
		Assert.assertTrue(a1 == a2);
		Assert.assertTrue(a1.b.c.d1.e == a2.b.c.d1.e);
	}
}