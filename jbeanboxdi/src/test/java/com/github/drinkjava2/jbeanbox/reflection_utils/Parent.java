package com.github.drinkjava2.jbeanbox.reflection_utils;

/**
 * @author Yong
 * @since 2.4
 */
@SuppressWarnings("unused")
public class Parent {

	public String publicField = "publicField";

	String defaultField = "defaultField";

	protected String protectedField = "protectedField";

	private String privateField = "privateField";

	private String userName;
	private Integer age;

	public String publicMethod() {
		return "publicMethod";
	}

	String defaultMethod() {
		return "defaultMethod";
	}

	protected String protectedMethod() {
		return "protectedMethod";
	}

	private String privateMethod() {
		return "privateMethod";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

}