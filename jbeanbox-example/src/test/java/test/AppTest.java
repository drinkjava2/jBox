package test;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
/**
 * @author Yong  Zhu
 * @since 2016-08-30
 *
 */
public class AppTest {
	@Test
	public void test1() {
		System.out.println("\r\n==============TEST EXAMPLE1===================");
		examples.example1_injections.Tester.main(null);
	}

	@Test
	public void test2() {
		System.out.println("\r\n==============TEST EXAMPLE2===================");
		examples.example2_aop.Tester.main(null);
	}

	@Test
	public void test3() {
		System.out.println("\r\n==============TEST EXAMPLE3===================");
		examples.example3_annotation.Tester.main(null);
	}

	@Test
	public void test4() {
		System.out.println("\r\n==============TEST EXAMPLE4===================");
		examples.example4_PostConstructorAndPreDestory.Tester.main(null);
	}

	@Test
	public void test5() {
		System.out.println("\r\n==============TEST EXAMPLE5===================");
		try {
			examples.example5_transaction.Tester.main(null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("==============================================================");
			System.out.println("Example5 test failed, please check your datasource setting");
			System.out.println("==============================================================");
		}
	}

	@Test
	public void test6() {
		System.out.println("\r\n==============TEST EXAMPLE6===================");
		try {
			examples.example6_type_safe.Tester.main(null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("==============================================================");
			System.out.println("Example6 test failed, please check your datasource setting");
			System.out.println("==============================================================");
		}
	}

	@Test
	public void test7() {
		System.out.println("\r\n==============TEST EXAMPLE7===================");
		examples.example7_annotation2.Tester.main(null);
	}

	@Test
	public void test8() {
		System.out.println("\r\n==============TEST EXAMPLE8===================");
		examples.example8_benchmark.Tester.main(null);
	}
}
