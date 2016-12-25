package test.test1_injections;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.BeanBox;

/**
 * @author Yong Zhu
 * @since Mar 1, 2016
 */
public class Tester {
	@Test
	public void doTest() {
 		Order order = BeanBox.getBean(Order.class);
		order.printALlItems();
		Order order1 = BeanBox.getBean(Order.class);
		Order order2 = BeanBox.getPrototypeBean(Order.class);// force return a prototype bean
		Assert.assertEquals(order, order1);
		Assert.assertNotEquals(order, order2);
	}
}