package test.test4_PostConstructorAndPreDestory;

import org.junit.Test;

import com.github.drinkjava2.BeanBox;

/**
 * @author Yong
 */
public class Tester1 {
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

	@Test
	public void doTest() {
		BeanBox.getBean(Tester1.class);
		BeanBox.defaultContext.close();// print Bye Sam
	}
}