package test.test1_injections;

/**
 *
 *
 * @author Yong Zhu
 * @since 2.4
 */
public class OrderItemFactory {
	private Boolean showPrice;

	public OrderItemFactory(Company company, Boolean hidePrice) {
		this.showPrice = hidePrice;
	}

	public static OrderItem createOrderItem(String itemName, Double price) {
		return new OrderItem(itemName, price);
	}

	public OrderItem createOrderItem2(Company company, String itemName, Double price) {
		if (!showPrice)
			price = 0.0;
		return new OrderItem(company.getName() + ":" + itemName, price);
	}
}