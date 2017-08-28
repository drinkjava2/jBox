package test.core_test.InnerClassProxy;

import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;

/**
 * This is to test TinyTx Declarative Transaction
 *
 * @author Yong Zhu
 *
 * @version 1.0.0
 * @since 1.0.0
 */

public class TheTester {
	public static class TheTargetBox extends BeanBox {
		{
			this.setClassOrValue(TheTarget.class);
		}
	}

	@Test
	public void doTest() {
		TheTarget tester = BeanBox.getBean(TheTargetBox.class);
		System.out.println(tester);
		tester.dosomething();
		BeanBox.defaultContext.close();// Release DataSource Pool
	}

}