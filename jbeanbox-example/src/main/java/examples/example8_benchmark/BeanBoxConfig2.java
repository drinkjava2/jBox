package examples.example8_benchmark;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.BeanBoxContext;

import examples.example8_benchmark.objects.A;
import examples.example8_benchmark.objects.B;
import examples.example8_benchmark.objects.C;
import examples.example8_benchmark.objects.D1;
import examples.example8_benchmark.objects.D2;
import examples.example8_benchmark.objects.E;

public class BeanBoxConfig2 {
	public static class PrototypeBox extends BeanBox {
		{
			this.setPrototype(true);
		}
	}

	public static class ABox extends PrototypeBox {
		public A create() {
			return new A((B) context.getBean(B.class)); // If use JDK8, no need write "(B)"
		}
	}

	public static class BBox extends PrototypeBox {
		public B create() {
			return new B((C) context.getBean(C.class));
		}
	}

	public static class CBox extends PrototypeBox {
		public C create() {
			return new C((D1) context.getBean(D1.class), (D2) context.getBean(D2.class));
		}
	}

	public static class D1Box extends PrototypeBox {
		public D1 create() {
			return new D1((E) context.getBean(E.class));
		}
	}

	public static class D2Box extends PrototypeBox {
		public D2 create() {
			return new D2((E) context.getBean(E.class));
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