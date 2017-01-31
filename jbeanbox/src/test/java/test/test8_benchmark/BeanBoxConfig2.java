package test.test8_benchmark;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.BeanBoxContext;

import test.test8_benchmark.objects.A;
import test.test8_benchmark.objects.B;
import test.test8_benchmark.objects.C;
import test.test8_benchmark.objects.D1;
import test.test8_benchmark.objects.D2;
import test.test8_benchmark.objects.E;

/**
 *
 *
 * @author Yong Zhu
 * @since 2.4
 */
public class BeanBoxConfig2 {
	public static class PrototypeBox extends BeanBox {
		{
			this.setPrototype(true);
		}
	}

	public static class ABox extends PrototypeBox {
		public A create() {
			return new A((B) getContext().getBean(B.class)); // If use JDK8, no need write "(B)"
		}
	}

	public static class BBox extends PrototypeBox {
		public B create() {
			return new B((C) getContext().getBean(C.class));
		}
	}

	public static class CBox extends PrototypeBox {
		public C create() {
			return new C((D1) getContext().getBean(D1.class), (D2) getContext().getBean(D2.class));
		}
	}

	public static class D1Box extends PrototypeBox {
		public D1 create() {
			return new D1((E) getContext().getBean(E.class));
		}
	}

	public static class D2Box extends PrototypeBox {
		public D2 create() {
			return new D2((E) getContext().getBean(E.class));
		}
	}

	public static class EBox extends PrototypeBox {
		public E create() {
			return new E();
		}
	}

	public static void main(String[] args) {
		BeanBoxContext ctx2 = new BeanBoxContext(BeanBoxConfig2.class).setIgnoreAnnotation(true);
		A a = ctx2.getBean(A.class);
		System.out.println(a.b.c.d1.e.getname());
	}

}