package examples.example8_benchmark;

import examples.example8_benchmark.objects.A;
import examples.example8_benchmark.objects.B;
import examples.example8_benchmark.objects.C;
import examples.example8_benchmark.objects.D1;
import examples.example8_benchmark.objects.D2;
import examples.example8_benchmark.objects.E;
import net.sf.jbeanbox.BeanBox;
import net.sf.jbeanbox.BeanBoxContext;

public class BeanBoxConfig1 {
	public static class ABox extends BeanBox {
		{
			this.setConstructor(A.class, B.class);
		}
	}

	public static class BBox extends BeanBox {
		{
			this.setConstructor(B.class, C.class);
		}
	}

	public static class CBox extends BeanBox {
		{
			this.setConstructor(C.class, D1.class, D2.class);
		}
	}

	public static class D1Box extends BeanBox {
		{
			this.setConstructor(D1.class, E.class);
		}
	}

	public static class D2Box extends BeanBox {
		{
			this.setConstructor(D2.class, E.class);
		}
	}

	public static void main(String[] args) {
		BeanBoxContext ctx1 = new BeanBoxContext(BeanBoxConfig1.class).setIgnoreAnnotation(true);
		A a = ctx1.getBean(A.class);
		System.out.println(a.b.c.d1.e.getname());
	}

}