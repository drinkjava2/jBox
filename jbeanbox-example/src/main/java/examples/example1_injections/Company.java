package examples.example1_injections;

public class Company {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void printName() {
		System.out.println(name);
	}

}