package test.core_test.NoProxy;

import com.github.drinkjava2.jbeanbox.BeanBox;

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