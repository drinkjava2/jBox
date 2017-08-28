package test.core_test.AopProxy;

import com.github.drinkjava2.jbeanbox.AopAround;

import test.logger_advice.AopInvokeAdvice;

/**
 * @author Yong
 * @since 2.4.5
 */
public class OneNOParamConstructor {
	private String name;
	private Integer age;

	public OneNOParamConstructor() {
		System.out.println("In OneNOParamConstructor constructor");
	};

	@AopAround(AopInvokeAdvice.class)
	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

}