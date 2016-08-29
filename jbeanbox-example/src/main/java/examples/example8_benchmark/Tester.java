package examples.example8_benchmark;

import examples.example8_benchmark.objects.A;
import examples.example8_benchmark.objects.B;
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
		System.out.printf("BenchMark Test, build Object tree %s times\r\n", repeattimes);

		BeanBoxContext ctx1 = new BeanBoxContext().addConfig(BeanBoxConfig.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			ctx1.getBean(A.class);
		}
		long end = System.currentTimeMillis();
		System.out.println(String.format("%8s|%6sms", "Config1", end - start));

		BeanBoxContext ctx2 = new BeanBoxContext().addConfig(BeanBoxConfig2.class);
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			ctx2.getBean(A.class);
		}
		end = System.currentTimeMillis();
		System.out.println(String.format("%8s|%6sms", "Config2", end - start));
	}
}