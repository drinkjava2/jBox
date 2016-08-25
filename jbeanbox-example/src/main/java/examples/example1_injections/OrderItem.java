package examples.example1_injections;

public class OrderItem {
	private String itemName;
	private Double price;

	public OrderItem(String itemName, Double price) {
		this.itemName = itemName;
		this.price = price;
	}

	public void outputItemInfo() {
		System.out.println(itemName + "  $" + price);
	}

}