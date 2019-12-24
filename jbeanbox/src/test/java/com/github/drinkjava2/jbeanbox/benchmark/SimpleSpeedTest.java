package com.github.drinkjava2.jbeanbox.benchmark;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.JBEANBOX;

/**
 * A simple benchmark test
 * 
 * @since 2.4
 */

public class SimpleSpeedTest {

	public static class A {
	}

	public static class myBeanBox extends BeanBox {
		{
			singleton = false;
		}

		public Object create() {
			return new A();
		}
	}

	@Test
	public void speedTest() throws InstantiationException, IllegalAccessException, SecurityException,
			NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

		long start = System.currentTimeMillis();
		for (int i = 0; i < 5000; i++) {// change to 5000000 to do real test
			JBEANBOX.getBean(myBeanBox.class);
		}
		long end = System.currentTimeMillis();
		String timeused = "" + ((end - start) * 1000) / 1000000.0;
		System.out.println(String.format("%20s: %6s s \r\n", "SimpleSpeedTest get prototype 5000 times: ", timeused));
	}

}