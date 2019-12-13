package com.github.drinkjava2.jbeanbox.benchmark;

import static com.github.drinkjava2.jbeanbox.JBEANBOX.inject;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.BeanContext;
import com.github.drinkjava2.jbeanbox.benchmark.objects.A;
import com.github.drinkjava2.jbeanbox.benchmark.objects.B;
import com.github.drinkjava2.jbeanbox.benchmark.objects.C;
import com.github.drinkjava2.jbeanbox.benchmark.objects.D1;
import com.github.drinkjava2.jbeanbox.benchmark.objects.D2;
import com.github.drinkjava2.jbeanbox.benchmark.objects.E;

/**
 * There are 3 ways to create bean: <br/>
 * 1. Set Constructor or set beanClass to use 0 parameter constructor <br/>
 * 2. Set a create method <br/>
 * 3. use Annotation to set constructor or create method <br/>
 * 
 * This is to test Java configuration (setAllowAnnotation=false)
 *
 * 
 *
 * @author Yong Zhu
 * @since 2.4
 */
public class BoxConfig1 {
	public static class PrototypeBox extends BeanBox {
		{
			setSingleton(false);
		}
	}

	public static class ABox extends PrototypeBox {
		{
			injectConstruct(A.class, B.class, inject(BBox.class));
		}
	}

	public static class BBox extends PrototypeBox {
		{
			injectConstruct(B.class, C.class, inject(CBox.class));
		}
	}

	public static class CBox extends PrototypeBox {
		{
			injectConstruct(C.class, D1.class, D2.class, inject(D1Box.class), inject(D2Box.class));
		}
	}

	public static class D1Box extends PrototypeBox {
		{
			injectConstruct(D1.class, E.class, inject(EBox.class));
		}
	}

	public static class D2Box extends PrototypeBox {
		{
			injectConstruct(D2.class, E.class, inject(EBox.class));
		}
	}

	public static class EBox extends PrototypeBox {
		{
			injectConstruct(E.class);
		}
	}

	public static void main(String[] args) {
		BeanContext ctx1 = new BeanContext();
		ctx1.setAllowAnnotation(false);
		A a = ctx1.getBean(ABox.class);
		System.out.println(a.b.c.d1.e.getname());
		System.out.println(a.b.c.d2.e.getname());
	}

}