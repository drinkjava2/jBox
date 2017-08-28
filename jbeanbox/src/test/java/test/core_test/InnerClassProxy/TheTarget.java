package test.core_test.InnerClassProxy;

import com.github.drinkjava2.jbeanbox.AopAround;

import test.logger_advice.AopInvokeAdvice;

/**
 * This is function test
 *
 * @author Yong Zhu
 *
 * @version 1.0.0
 * @since 1.0.0
 */

public class TheTarget {

	@AopAround(AopInvokeAdvice.class)
	public void dosomething() {
		System.out.println("do something");
	}

}