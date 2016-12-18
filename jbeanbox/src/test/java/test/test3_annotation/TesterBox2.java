package test.test3_annotation;

import com.github.drinkjava2.BeanBox;

public class TesterBox2 extends BeanBox {
	{
		this.setClassOrValue(Tester.class);
		this.setProperty("s8", "Hi 8");// case 8
		this.setProperty("s9", "Hi 9");// //case 8
	}

	public static class StringBox2 extends BeanBox {// case 5
		{
			this.setClassOrValue("Hi 5");
		}
	}
}
