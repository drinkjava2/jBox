package test.core_test.AopProxy;

import com.github.drinkjava2.jbeanbox.AopAround;
import com.github.drinkjava2.jbeanbox.BeanBox;

import test.logger_advice.AopInvokeAdvice;

/**
 * @author Yong 
 * @since 2.4.5
 */
public class OneWithParamConstructor { 
	private String name;
	private Integer age;

	public OneWithParamConstructor(String name, Integer age) {
		System.out.println("In OneWithParamConstructor constructor");
		this.name = name;
		this.age = age;
	}

	@AopAround(AopInvokeAdvice.class)
	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

	public static class OneWithParamConstructorBox extends BeanBox {
		{
			this.setConstructor(OneWithParamConstructor.class, "Tom", 3);
		}
	}
}