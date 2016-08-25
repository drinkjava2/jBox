package examples.example1_injections;

import net.sf.jbeanbox.BeanBox;

/**
 * @author Yong Zhu
 * @since Mar 1, 2016
 */
public class Tester {
	public static void main(String[] args) {
		Order order = BeanBox.getBean(Order.class);
		order.printALlItems();
		System.out.println("Order bean is a SingleTon? " + (order == OrderBox.getBean(OrderBox.class)));
	}
}