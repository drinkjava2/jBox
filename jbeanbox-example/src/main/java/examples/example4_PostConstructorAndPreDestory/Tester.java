package examples.example4_PostConstructorAndPreDestory;

import net.sf.jbeanbox.BeanBox;

/**
 * @author Yong
 */
public class Tester {
	private String name;

	public void init() {
		name = "Sam";
	}

	public void destory() {
		System.out.println("Bye " + name);
	}

	public static class TesterBox extends BeanBox {
		{
			setPostConstructor("init");
			setPreDestory("destory");
		}
	}

	public static void main(String[] args) {
		BeanBox.getBean(Tester.class);
		BeanBox.defaultContext.close();// print Bye Sam
	}
}