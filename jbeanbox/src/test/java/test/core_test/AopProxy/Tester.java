package test.core_test.AopProxy;

import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;

/**
 * @author Yong
 * @since 2.4.5
 */
public class Tester {

	@Test
	public void doTest() {
		WithConstructorTarget t1 = BeanBox.getBean(WithConstructorTarget.class);
		System.out.println("result1=" + t1.getNameAndAge() + "\r");

		WithConstructorTarget2 t2 = new BeanBox().setConstructor(WithConstructorTarget2.class, "Jerry", 10).getBean();
		System.out.println("result2=" + t2.getNameAndAge() + "\r");

		WithConstructorTarget2 t3 = new BeanBox().setClassOrValue(WithConstructorTarget2.class).getBean();
		System.out.println("result2=" + t3.getNameAndAge() + "\r");

		WithConstructorTarget3 t4 = new BeanBox().setClassOrValue(WithConstructorTarget3.class).getBean();
		System.out.println("result2=" + t4.getNameAndAge() + "\r");

		NoConstructorTarget t5 = BeanBox.getBean(NoConstructorTarget.class);
		System.out.println("result3=" + t5.getNameAndAge() + "\r");

		NoConstructorTarget2 t6 = BeanBox.getBean(NoConstructorTarget2.class);
		System.out.println("result3=" + t6.getNameAndAge() + "\r");
	}
}