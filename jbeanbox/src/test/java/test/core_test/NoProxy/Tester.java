package test.core_test.NoProxy;

import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;

import test.core_test.NoProxy.SubClassParamConstructor.A;
import test.core_test.NoProxy.SubClassParamConstructor.B;

/**
 * @author Yong
 * @since 2.4.5
 */
public class Tester {

	@Test
	public void doTest() {
		// ==== have constructor===
		OneWithParamConstructor t1 = BeanBox.getBean(OneWithParamConstructor.class);
		System.out.println("result1=" + t1.getNameAndAge() + "\r");

		TwoParamConstructor t2 = new BeanBox().setConstructor(TwoParamConstructor.class, "Jerry", 10).getBean();
		System.out.println("result2=" + t2.getNameAndAge() + "\r");

		TwoParamConstructor t3 = new BeanBox().setClassOrValue(TwoParamConstructor.class).getBean();
		System.out.println("result3=" + t3.getNameAndAge() + "\r");

		OneNOParamConstructor t4 = new BeanBox().setClassOrValue(OneNOParamConstructor.class).getBean();
		System.out.println("result4=" + t4.getNameAndAge() + "\r");

		// ==== no constructor===
		NoConstructor t5 = BeanBox.getBean(NoConstructor.class);
		System.out.println("result5=" + t5.getNameAndAge() + "\r");

		NoConstructor2 t6 = BeanBox.getBean(NoConstructor2.class);
		System.out.println("result6=" + t6.getNameAndAge() + "\r");

		// ==== sub class parameter constructor ===
		SubClassParamConstructor t7 = new BeanBox().setConstructor(SubClassParamConstructor.class, new A()).getBean();
		System.out.println("result7=" + t7.getA() + "\r");

		SubClassParamConstructor t8 = new BeanBox().setConstructor(SubClassParamConstructor.class, new B())
				.setConstructorTypes(A.class).getBean();
		System.out.println("result8=" + t8.getA() + "\r");

	}
}