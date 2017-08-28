package test.core_test.NoProxy;

/**
 * @author Yong
 * @since 2.4.5
 */
public class SubClassParamConstructor {
	private A a;

	public SubClassParamConstructor(A a) {
		this.a = a;
	}

	public String getA() {
		System.out.println("a=" + a);
		return "a=" + a;
	}

	public static class A {

	}

	public static class B extends A {

	}

}