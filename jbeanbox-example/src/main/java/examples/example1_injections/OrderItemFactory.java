package examples.example1_injections;

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