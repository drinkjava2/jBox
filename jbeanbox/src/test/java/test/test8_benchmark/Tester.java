package test.test8_benchmark;

import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBoxContext;

import test.test8_benchmark.objects.A;
import test.test8_benchmark.objects.B;

/**
 * A simple benchmark test
 * @since 2.4
 */

public class Tester {
	@Before
	public void beforeTest() {
		System.out.println("=========  A simple benchmark test =========");
	}

	public static class AA {
		AA(A a, B b) {
		}

		public void print() {
			System.out.println("AA");
		}
	}

	private static String singltTonORPrototype(A aold, A a) {
		String type;
		type = (aold == a) ? "(Singlton)" : "(Prototype)";
		return type;
	}

	@Test
	public void doTest() {
		main(null);
	}

	public static void main(String[] args) {
		long repeattimes = 100;
		System.out.printf("BenchMark Test, build Object tree %s times\r\n", repeattimes);
		A aold = null, a = null;

		// #1 use config1, normal configuration
		BeanBoxContext ctx = new BeanBoxContext(BeanBoxConfig1.class).setIgnoreAnnotation(true);
		aold = ctx.getBean(A.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			a = ctx.getBean(A.class);
		}
		long end = System.currentTimeMillis();
		String type = (aold == a) ? "(Singlton)" : "(Prototype)";
		System.out.println(String.format("%45s|%6sms", "BeanBox Normal Configuration" + type, end - start));

		// #2 use config2, java type safe configuration
		ctx = new BeanBoxContext(BeanBoxConfig2.class).setIgnoreAnnotation(true);
		aold = ctx.getBean(A.class);
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			a = ctx.getBean(A.class);
		}
		end = System.currentTimeMillis();
		type = singltTonORPrototype(aold, a);
		System.out.println(String.format("%45s|%6sms", "BeanBox Type Safe Configuration" + type, end - start));

		// #3 use config3, only annotations
		ctx = new BeanBoxContext();
		aold = ctx.getBean(A.class);
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			a = ctx.getBean(A.class);
		}
		end = System.currentTimeMillis();
		type = singltTonORPrototype(aold, a);
		System.out.println(String.format("%45s|%6sms", "BeanBox Annotation Only" + type, end - start));

		// #4 use config1, normal configuration
		ctx = new BeanBoxContext(BeanBoxConfig1.class).setIgnoreAnnotation(true);
		aold = ctx.getSingletonBean(A.class);// force return a singleton bean
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			a = ctx.getSingletonBean(A.class);
		}
		end = System.currentTimeMillis();
		type = singltTonORPrototype(aold, a);
		System.out.println(String.format("%45s|%6sms", "BeanBox Normal Configuration" + type, end - start));

		// #5 use config2, java type safe configuration
		ctx = new BeanBoxContext(BeanBoxConfig2.class).setIgnoreAnnotation(true);
		aold = ctx.getSingletonBean(A.class); // force return a singleton bean
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			a = ctx.getSingletonBean(A.class);
		}
		end = System.currentTimeMillis();
		type = singltTonORPrototype(aold, a);
		System.out.println(String.format("%45s|%6sms", "BeanBox Type Safe Configuration" + type, end - start));

		// #6 use config3, only annotations
		ctx = new BeanBoxContext();
		aold = ctx.getSingletonBean(A.class);// force return a singleton bean
		start = System.currentTimeMillis();
		for (int i = 0; i < repeattimes; i++) {
			a = ctx.getSingletonBean(A.class);
		}
		end = System.currentTimeMillis();
		type = singltTonORPrototype(aold, a);
		System.out.println(String.format("%45s|%6sms", "BeanBox Annotation Only" + type, end - start));
	}

}