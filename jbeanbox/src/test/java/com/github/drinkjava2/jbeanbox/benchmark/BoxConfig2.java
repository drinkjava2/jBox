package com.github.drinkjava2.jbeanbox.benchmark;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.BeanBoxContext;
import com.github.drinkjava2.jbeanbox.Require;
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
 * Here to test Create method (setAllowAnnotation=false)
 *
 * @author Yong Zhu
 * @since 2.4
 */
public class BoxConfig2 {
	public static class PrototypeBox extends BeanBox {
		{
			this.setSingleton(false);
		}
	}

	public static class ABox extends PrototypeBox {
		public Object create(Require caller) {
			return new A((B) caller.getBean(BBox.class));
		}
	}

	public static class BBox extends PrototypeBox {
		public Object create(Require caller) {
			return new B((C) caller.getBean(CBox.class));
		}
	}

	public static class CBox extends PrototypeBox {
		public Object create(Require caller) {
			return new C((D1) caller.getBean(D1Box.class), (D2) caller.getBean(D2Box.class));
		}
	}

	public static class D1Box extends PrototypeBox {
		public Object create(Require caller) {
			return new D1((E) caller.getBean(EBox.class));
		}
	}

	public static class D2Box extends PrototypeBox {
		public Object create(Require caller) {
			return new D2((E) caller.getBean(EBox.class));
		}
	}

	public static class EBox extends PrototypeBox {
		public Object create() {
			return new E();
		}
	}

	public static void main(String[] args) {
		BeanBoxContext ctx2 = new BeanBoxContext();
		ctx2.setAllowAnnotation(false);
		ctx2.bind(A.class, ABox.class);
		A a = ctx2.getBean(A.class);
		System.out.println(a.b.c.d1.e.getname());
		System.out.println(a.b.c.d2.e.getname());
	}

}