
package com.github.drinkjava2.jbeanbox.reflection_utils;

import static com.github.drinkjava2.jbeanbox.JBEANBOX.value;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.JBEANBOX;

/**
 * This is to test a parent have a create method but child do not have
 *
 * @author Yong Zhu
 *
 * @since 2.4
 */
public class CreateMethodTest {

	public static class BaseClassBox extends BeanBox {
		public Object create() {
			Parent p = new Parent();
			return p;
		}
	}

	public static class childClassBox extends BaseClassBox {
		{
			this.setBeanClass(Parent.class);
			this.injectField("userName", value("u1"));
		}
	}

	@Test
	public void test() {
		Parent p = JBEANBOX.getBean(childClassBox.class);
		Assert.assertEquals("u1", p.getUserName());
	}

}