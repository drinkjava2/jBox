package test.core_test.InnerClassProxy;

import org.junit.Test;

import com.github.drinkjava2.jbeanbox.AopAround;
import com.github.drinkjava2.jbeanbox.BeanBox;

import test.logger_advice.AopInvokeAdvice;

/**
 * This is to test TinyTx Declarative Transaction
 *
 * @author Yong Zhu
 *
 * @version 1.0.0
 * @since 1.0.0
 */

public class TheTester2 {
	public static class TheInnerTargetBox extends BeanBox {
		{
			this.setClassOrValue(TheInnerTarget.class);
		}
	}

	public static class TheInnerTarget {

		@AopAround(AopInvokeAdvice.class)
		public void dosomething() {
			System.out.println("do something");
		}
	}

	@Test
	public void doTest() {
		TheInnerTarget tester = BeanBox.getBean(TheInnerTargetBox.class);
		System.out.println(tester);
		tester.dosomething();
		BeanBox.defaultContext.close();// Release DataSource Pool
	}

}