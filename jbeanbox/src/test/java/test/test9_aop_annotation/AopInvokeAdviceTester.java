package test.test9_aop_annotation;

import org.junit.Test;

import com.github.drinkjava2.jbeanbox.AopAround;
import com.github.drinkjava2.jbeanbox.BeanBox;

import test.test9_aop_annotation.AopInvokeAdvice.AopInvokeAdviceBox;

/**
 * An example of AOP alliance Advice
 * 
 * @author Yong Zhu
 * @since 2.4
 */
public class AopInvokeAdviceTester {

	@AopAround(AopInvokeAdvice.class)
	public void method1() {
		System.out.println("in method1");
	}

	@AopAround(AopInvokeAdvice.class)
	public void method2() {
		System.out.println("in method2");
	}

	@AopAround(AopInvokeAdviceBox.class)
	public void method3() {
		System.out.println("in method3");
	}

	@AopAround(AopInvokeAdviceBox2.class)
	public void method4() {
		System.out.println("in method4");
		method1();
		method2();
		method3();
	}

	@Test
	public void doTest() {
		BeanBox.defaultContext.close();
		AopInvokeAdviceTester t = BeanBox.getBean(AopInvokeAdviceTester.class);
		t.method4();
	}
}