package test.core_test.AopProxy;

import com.github.drinkjava2.jbeanbox.AopAround;
import com.github.drinkjava2.jbeanbox.BeanBox;

import test.test9_aop_annotation.AopInvokeAdvice;

/**
 * @author Yong 
 * @since 2.4.5
 */
public class WithConstructorTarget { 
	private String name;
	private Integer age;

	public WithConstructorTarget(String name, Integer age) {
		System.out.println("In WithConstructorTarget constructor");
		this.name = name;
		this.age = age;
	}

	@AopAround(AopInvokeAdvice.class)
	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

	public static class WithConstructorTargetBox extends BeanBox {
		{
			this.setConstructor(WithConstructorTarget.class, "Tom", 3);
		}
	}
}