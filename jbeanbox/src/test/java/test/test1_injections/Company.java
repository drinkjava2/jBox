package test.test1_injections;

/**
 *
 *
 * @author Yong Zhu
 * @since 2.4
 */
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