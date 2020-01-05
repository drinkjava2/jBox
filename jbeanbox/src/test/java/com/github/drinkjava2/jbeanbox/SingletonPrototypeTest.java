
package com.github.drinkjava2.jbeanbox;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.annotation.PROTOTYPE;

public class SingletonPrototypeTest {

	public static class A {

	}

	public static class SingleTonBox extends BeanBox {
		{
			setBeanClass(A.class);
		}
	}

	public static class PrototypeBox extends BeanBox {
		{
			setBeanClass(A.class);
			setPrototype(true);
		}
	}

	@Test
	public void testSingleton() {
		Assert.assertTrue(new SingleTonBox().isSingleton());
		Assert.assertTrue(JBEANBOX.getBeanBox(SingleTonBox.class) == JBEANBOX.getBeanBox(SingleTonBox.class));

		BeanBox box1 = new SingleTonBox();
		BeanBox box2 = new SingleTonBox();
		Assert.assertTrue(box1.getBean() == box1.getBean());
		Assert.assertTrue(box1.getBean() != box2.getBean());
	}

	@Test
	public void testPrototype() {
		Assert.assertFalse(new PrototypeBox().isSingleton());
		Assert.assertTrue(JBEANBOX.getBeanBox(PrototypeBox.class) == JBEANBOX.getBeanBox(PrototypeBox.class));

		BeanBox box = new PrototypeBox();
		Assert.assertTrue(box.getBean() != box.getBean());
	}

	@PROTOTYPE
	public static class B {
	}

	public static class C implements PrototypeBean {
	}

	@Test
	public void testPrototypeAnno() {
		BeanBox box1 = JBEANBOX.getBeanBox(B.class);
		BeanBox box2 = JBEANBOX.getBeanBox(C.class);
		Assert.assertFalse(box1.isSingleton());
		Assert.assertFalse(box2.isSingleton());
		Assert.assertTrue(box1.getBean() != box1.getBean());
		Assert.assertTrue(box2.getBean() != box2.getBean());
	}

}