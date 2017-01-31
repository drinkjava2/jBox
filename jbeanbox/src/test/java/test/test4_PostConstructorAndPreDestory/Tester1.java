package test.test4_PostConstructorAndPreDestory;

import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;

/**
 * @author Yong
 * @since 2.4
 */
public class Tester1 {
	@Before
	public void beforeTest() {
		System.out.println("========= PostConstructor & PreDestory method test =========");
	}

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