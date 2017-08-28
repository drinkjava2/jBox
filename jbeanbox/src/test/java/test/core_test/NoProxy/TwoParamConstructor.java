package test.core_test.NoProxy;

/**
 * @author Yong
 * @since 2.4.5
 */
public class TwoParamConstructor {
	private String name;
	private Integer age;

	public TwoParamConstructor() {
		System.out.println("In TwoParamConstructor constructor1");
	};

	public TwoParamConstructor(String name, Integer age) {
		System.out.println("In WithConstructorTarget2 constructor2");
		this.name = name;
		this.age = age;
	}

	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

}