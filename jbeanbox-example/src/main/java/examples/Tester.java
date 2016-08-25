package examples;

/**
 * @author Yong
 */
public class Tester {
	public static void main(String[] args) {
		examples.example1_injections.Tester.main(null);
		System.out.println("====================================================");
		examples.example2_aop.Tester.main(null);
		System.out.println("====================================================");
		examples.example3_annotation.Tester.main(null);
		System.out.println("====================================================");
		examples.example4_PostConstructorAndPreDestory.Tester.main(null);
		System.out.println("====================================================");
		examples.example5_transaction.Tester.main(null);
	}
}