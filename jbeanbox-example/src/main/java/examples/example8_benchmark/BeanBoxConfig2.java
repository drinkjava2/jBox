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
	public static class ABox extends BeanBox {
		public A create() {
			return new A((B) new BBox().getBean()); // If use JDK8, no need write "(B)"
		}
	}

	public static class BBox extends BeanBox {
		public B create() {
			this.setPrototype(true);
			return new B((C) new CBox().getBean());
		}
	}

	public static class CBox extends BeanBox {
		public C create() {
			this.setPrototype(true);
			return new C((D1) new D1Box().getBean(), (D2) new D2Box().getBean());
		}
	}

	public static class D1Box extends BeanBox {
		public D1 create() {
			this.setPrototype(true);
			return new D1((E) new EBox().getBean());
		}
	}

	public static class D2Box extends BeanBox {
		public D2 create() {
			this.setPrototype(true);
			return new D2((E) new EBox().getBean());
		}
	}

	public static class EBox extends BeanBox {
		public E create() {
			this.setPrototype(true);
			return new E();
		}
	}

	public static void main(String[] args) {
		BeanBoxContext ctx2 = new BeanBoxContext(BeanBoxConfig2.class).setIgnoreAnnotation(true);
		A a = ctx2.getBean(A.class);
		System.out.println(a.b.c.d1.e.getname());
	}

}