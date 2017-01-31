package test.test4_PostConstructorAndPreDestory;

import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;

/**
 * Test setPostConstructor() and setPreDestory() method for a Child class
 * 
 * @author Yong
 * @since 2.4
 */
public class Tester2 {
	@Before
	public void beforeTest() {
		System.out.println("=========Test setPostConstructor() and setPreDestory() method for a Child class =========");
	}

	@Test
	public void doTest() {
		BeanBox.getBean(Child.class);
		BeanBox.defaultContext.close();// print Bye Sam
	}
	
	public static void main(String[] args) {
		BeanBox.getBean(Child.class);
		BeanBox.defaultContext.close();// print Bye Sam
	}
}