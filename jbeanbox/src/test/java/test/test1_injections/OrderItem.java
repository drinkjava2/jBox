package test.test1_injections;

/**
 *
 *
 * @author Yong Zhu
 * @since 2.4
 */
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