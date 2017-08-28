package test.core_test.AopProxy;

import com.github.drinkjava2.jbeanbox.AopAround;

import test.test9_aop_annotation.AopInvokeAdvice;

/**
 * @author Yong
 * @since 2.4.5
 */
public class SubClassParamConstructor {
	private A a;

	public SubClassParamConstructor(A a) {
		this.a = a;
	}

	@AopAround(AopInvokeAdvice.class)
	public String getA() {
		System.out.println("a=" + a);
		return "a=" + a;
	}

	public static class A {

	}

	public static class B extends A {

	}

}