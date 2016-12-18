package test.test4_PostConstructorAndPreDestory;

import org.junit.Test;

import com.github.drinkjava2.BeanBox;

/**
 * Test setPostConstructor() and setPreDestory() method for a Child class
 * 
 * @author Yong
 */
public class Tester2 {

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