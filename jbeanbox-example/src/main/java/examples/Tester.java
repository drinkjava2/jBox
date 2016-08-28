package examples;

public class Tester {
	public static void main(String[] args) {
		System.out.println("\r\n==============TEST EXAMPLE1===================");
		examples.example1_injections.Tester.main(null);
		System.out.println("\r\n==============TEST EXAMPLE2===================");
		examples.example2_aop.Tester.main(null);
		System.out.println("\r\n==============TEST EXAMPLE3===================");
		examples.example3_annotation.Tester.main(null);
		System.out.println("\r\n==============TEST EXAMPLE4===================");
		examples.example4_PostConstructorAndPreDestory.Tester.main(null);
		System.out.println("\r\n==============TEST EXAMPLE5===================");
		examples.example5_transaction.Tester.main(null);
		System.out.println("\r\n==============TEST EXAMPLE6===================");
		examples.example6_type_safe.Tester.main(null);
	}
}
