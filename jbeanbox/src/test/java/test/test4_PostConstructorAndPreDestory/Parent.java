package test.test4_PostConstructorAndPreDestory;

/**
 * @author Yong
 * @since 2.4
 */
public class Parent {
	private String name;

	public void init() {
		name = "Sam";
	}

	public void destory() {
		System.out.println("Bye " + name);
	}

}