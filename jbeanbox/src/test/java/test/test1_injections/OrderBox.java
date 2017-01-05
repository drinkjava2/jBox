package test.test1_injections;

import com.github.drinkjava2.BeanBox;

/**
 * @author Yong Zhu
 * @since 2.4
 */
public class OrderBox extends BeanBox {// Java Configuration class, play the same role like XML in Spring
	{ // setClassOrValue(Order.class); //if called by getBean(), no need set class
		setProperty("orderNO", "PO#20160214");// Inject value
		setProperty("company", CompanyBox.class);// Inject a bean
		setProperty("orderItem1", new BeanBox(OrderItem.class, "Dog1", 77.99));// inject constructor parameters
		setStaticFactory("orderItem2", OrderItemFactory.class, "createOrderItem", "Dog2", 88.99);// static factory
		BeanBox factory = new BeanBox(OrderItemFactory.class, new CompanyBox(), false);
		setBeanFactory("orderItem3", factory, "createOrderItem2", new CompanyBox(), "Dog3", 99.99);// bean factory
	}

	public static class CompanyBox1 extends BeanBox {
		{
			setClassOrValue(Company.class);
			setProperty("name", "Company1");
		}
	}

	public static class CompanyBox extends CompanyBox1 {// Extends from another Bean configuration
		{
			setProperty("name", "Company2");// property override
		}
	}
}
