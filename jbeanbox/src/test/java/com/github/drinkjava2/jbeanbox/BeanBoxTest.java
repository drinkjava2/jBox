package com.github.drinkjava2.jbeanbox;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * BeanBoxTest
 *
 * @author Yong Zhu
 * @since 2.4.7
 */
public class BeanBoxTest {
	@Before
	public void init() {
		BeanBoxContext.reset();
	}

	@Test
	public void newCopy() {
		BeanBox box = new BeanBox().newCopy();
		Assert.assertNotNull(box);
	}

	@Test
	public void getDebugInfo() {
		BeanBox box = new BeanBox();
		Assert.assertNotNull(box.getDebugInfo());
	}

}