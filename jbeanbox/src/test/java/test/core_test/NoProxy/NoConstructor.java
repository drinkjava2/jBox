package test.core_test.NoProxy;

import com.github.drinkjava2.jbeanbox.BeanBox;

/**
 * @author Yong
 * @since 2.4.5
 */
public class NoConstructor {
	private String name;
	private Integer age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

	public static class NoConstructorBox extends BeanBox {
		{
			this.setClassOrValue(NoConstructor.class);
			this.setProperty("name", "Jerry");
			this.setProperty("age", 4);
		}
	}
}