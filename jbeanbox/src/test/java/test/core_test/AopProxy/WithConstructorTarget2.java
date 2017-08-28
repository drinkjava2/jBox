package test.core_test.AopProxy;

import com.github.drinkjava2.jbeanbox.AopAround;

import test.test9_aop_annotation.AopInvokeAdvice;

/**
 * @author Yong
 * @since 2.4.5
 */
public class WithConstructorTarget2 { 
	private String name;
	private Integer age;

	public WithConstructorTarget2() {
		System.out.println("In WithConstructorTarget2 constructor1");
	};

	public WithConstructorTarget2(String name, Integer age) {
		System.out.println("In WithConstructorTarget2 constructor2");
		this.name = name;
		this.age = age;
	}

	@AopAround(AopInvokeAdvice.class)
	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

}