package examples.example8_benchmark;

import examples.example8_benchmark.objects.A;
import examples.example8_benchmark.objects.B;
import net.sf.jbeanbox.BeanBox;
import net.sf.jbeanbox.BeanBoxContext;

/**
 * This example is similar like example5, but in "TesterBox" use Java type safe configurations to create bean instance.
 * <br/>
 * 
 * @author Yong Zhu
 * @since 2016-8-23
 */

public class Tester {

	public static class AA {
		AA(A a, B b) {
		}

		public void print() {
			System.out.println("AA");
		}
	}

	public static void main(String[] args) {
		long repeattimes = 50000;
		System.out.printf("BenchMark Test, build Object tree %s times\r\n\r\n", repeattimes);
		String result1 = null, result2 = null, result3 = null;

		// use config1, normal configuration
		BeanBoxContext ctx1 = new BeanBoxContext(BeanBoxConfig1.class).setIgnoreAnnotation(true);
		long start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			A a = ctx1.getBean(A.class);
			result1 = a.b.c.d1.e.getname();
		}
		long end = System.currentTimeMillis();
		System.out.println(String.format("%35s|%6sms", "BeanBox Normal Configuration", end - start));

		// use config2, java type safe configuration
		BeanBoxContext ctx2 = new BeanBoxContext(BeanBoxConfig2.class).setIgnoreAnnotation(true);
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			A a = ctx2.getBean(A.class);
			result2 = a.b.c.d1.e.getname();
		}
		end = System.currentTimeMillis();
		System.out.println(String.format("%35s|%6sms", "BeanBox Type Safe Configuration", end - start));

		// use config3, only annotations
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			A a = BeanBox.defaultBeanBoxContext.getBean(A.class);
			result3 = a.b.c.d1.e.getname();
		}
		end = System.currentTimeMillis();
		System.out.println(String.format("%35s|%6sms", "BeanBox Annotation Only", end - start));

		System.out.println("\r\nresult1=" + result1);
		System.out.println("result2=" + result2);
		System.out.println("result3=" + result3);

	}
}