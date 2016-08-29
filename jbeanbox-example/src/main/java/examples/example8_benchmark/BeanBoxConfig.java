package examples.example8_benchmark;

import examples.example8_benchmark.objects.A;
import examples.example8_benchmark.objects.B;
import examples.example8_benchmark.objects.C;
import examples.example8_benchmark.objects.D1;
import examples.example8_benchmark.objects.D2;
import examples.example8_benchmark.objects.E;
import net.sf.jbeanbox.BeanBox;

public class BeanBoxConfig {
	public static class ABox extends BeanBox {
		{
			this.setConstructor(A.class, new BBox());
		}
	}

	public static class BBox extends BeanBox {
		{
			this.setConstructor(B.class, new CBox());
		}
	}

	public static class CBox extends BeanBox {
		{
			this.setConstructor(C.class, new D1Box(), new D2Box());
		}
	}

	public static class D1Box extends BeanBox {
		{
			this.setConstructor(D1.class, new BeanBox(E.class));
		}
	}

	public static class D2Box extends BeanBox {
		{
			this.setConstructor(D2.class, new BeanBox(E.class));
		}
	}

}