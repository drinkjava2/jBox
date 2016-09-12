package examples.example3_annotation;

import com.github.drinkjava2.BeanBox;

public class A {

	public static class StrBox extends BeanBox {
		{
			this.setClassOrValue("Hello1");
		}
	}

	public static class StringBox extends BeanBox {
		{
			this.setClassOrValue("Hello2");
		}
	}

	public static class StringBox2 extends BeanBox {
		{
			this.setClassOrValue("Hi 2");
		}
	}
}
