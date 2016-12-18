package test.core_test.reflection_utils;

/**
 * @author Yong
 */
@SuppressWarnings("unused")
public class Parent {

	public String publicField = "publicField";

	String defaultField = "defaultField";

	protected String protectedField = "protectedField";

	private String privateField = "privateField";

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
}