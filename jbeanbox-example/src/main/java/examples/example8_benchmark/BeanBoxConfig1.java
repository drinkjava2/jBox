package examples.example8_benchmark;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.BeanBoxContext;

import examples.example8_benchmark.objects.A;
import examples.example8_benchmark.objects.B;
import examples.example8_benchmark.objects.C;
import examples.example8_benchmark.objects.D1;
import examples.example8_benchmark.objects.D2;
import examples.example8_benchmark.objects.E;

public class BeanBoxConfig1 {
	public static class PrototypeBox extends BeanBox {
		{
			this.setPrototype(true);
		}
	}

	public static class ABox extends PrototypeBox {
		{
			this.setConstructor(A.class, B.class);
		}
	}

	public static class BBox extends PrototypeBox {
		{
			this.setConstructor(B.class, C.class);
		}
	}

	public static class CBox extends PrototypeBox {
		{
			this.setConstructor(C.class, D1.class, D2.class);
		}
	}

	public static class D1Box extends PrototypeBox {
		{
			this.setConstructor(D1.class, E.class);
		}
	}

	public static class D2Box extends PrototypeBox {
		{
			this.setConstructor(D2.class, E.class);
		}
	}

	public static class EBox extends PrototypeBox {
		{
			this.setClassOrValue(E.class);
		}
	}

	public static void main(String[] args) {
		BeanBoxContext ctx1 = new BeanBoxContext(BeanBoxConfig1.class).setIgnoreAnnotation(true);
		A a = ctx1.getBean(A.class);
		System.out.println(a.b.c.d1.e.getname());
	}

}