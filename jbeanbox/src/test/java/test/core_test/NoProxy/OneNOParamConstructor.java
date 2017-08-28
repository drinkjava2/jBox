package test.core_test.NoProxy;

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

	public String getNameAndAge() {
		System.out.println("name=" + name + ", age=" + age);
		return "name=" + name + ", age=" + age;
	}

}