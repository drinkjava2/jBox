package examples.example8_benchmark;

import examples.example8_benchmark.objects.A;
import examples.example8_benchmark.objects.B;
import examples.example8_benchmark.objects.C;
import examples.example8_benchmark.objects.D1;
import examples.example8_benchmark.objects.D2;
import examples.example8_benchmark.objects.E;
import net.sf.jbeanbox.BeanBox;
import net.sf.jbeanbox.BeanBoxContext;

public class BeanBoxConfig2 {
	public static class ABox extends BeanBox {
		public A create() {
			return new A((B) new BBox().getBean()); // If use JDK8, no need write "(B)"
		}
	}

	public static class BBox extends BeanBox {
		public B create() {
			return new B((C) new CBox().getBean());
		}
	}

	public static class CBox extends BeanBox {
		public C create() {
			return new C((D1) new D1Box().getBean(), (D2) new D2Box().getBean());
		}
	}

	public static class D1Box extends BeanBox {
		public D1 create() {
			return new D1((E) new EBox().getBean());
		}
	}

	public static class D2Box extends BeanBox {
		public D2 create() {
			return new D2((E) new EBox().getBean());
		}
	}

	public static class EBox extends BeanBox {
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