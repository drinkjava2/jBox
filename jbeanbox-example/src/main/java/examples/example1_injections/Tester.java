package examples.example1_injections;

import com.github.drinkjava2.BeanBox;

/**
 * @author Yong Zhu
 * @since Mar 1, 2016
 */
public class Tester {
	public static void main(String[] args) {
		Order order = BeanBox.getBean(Order.class);
		order.printALlItems();
		Order order1 = BeanBox.getBean(Order.class);
		Order order2 = BeanBox.getPrototypeBean(Order.class);// force return a prototype bean
		System.out.println("Order1 is SingleTon? " + (order == order1));
		System.out.println("Order2 is SingleTon? " + (order == order2));

	}
}