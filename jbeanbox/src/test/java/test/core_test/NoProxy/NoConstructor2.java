package test.core_test.NoProxy;

/**
 * @author Yong
 * @since 2.4.5
 */
public class NoConstructor2 {

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
}
