package test.core_test.AopProxy;

import com.github.drinkjava2.jbeanbox.AopAround;

import test.test9_aop_annotation.AopInvokeAdvice;

/**
 * @author Yong
 * @since 2.4.5
 */
public class WithConstructorTarget3 {
	private String name;
	private Integer age;

	public WithConstructorTarget3() {
		System.out.println("In WithConstructorTarget3 constructor");
	};

	@AopAround(AopInvokeAdvice.class)
	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

}