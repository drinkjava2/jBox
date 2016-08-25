package examples.example1_injections;

public class Order {
	private String orderNO;
	private OrderItem orderItem1, orderItem2, orderItem3;
	private Company company;

	public void setOrderNO(String orderNO) {
		this.orderNO = orderNO;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public OrderItem getOrderItem1() {
		return orderItem1;
	}

	public void setOrderItem1(OrderItem orderItem1) {
		this.orderItem1 = orderItem1;
	}

	public OrderItem getOrderItem2() {
		return orderItem2;
	}

	public void setOrderItem2(OrderItem orderItem2) {
		this.orderItem2 = orderItem2;
	}

	public OrderItem getOrderItem3() {
		return orderItem3;
	}

	public void setOrderItem3(OrderItem orderItem3) {
		this.orderItem3 = orderItem3;
	}

	public String getOrderNO() {
		return orderNO;
	}

	public Company getCompany() {
		return company;
	}

	public void printALlItems() {
		System.out.println("OrderNO:" + orderNO);
		this.getCompany().printName();
		orderItem1.outputItemInfo();
		orderItem2.outputItemInfo();
		orderItem3.outputItemInfo();
	}

}