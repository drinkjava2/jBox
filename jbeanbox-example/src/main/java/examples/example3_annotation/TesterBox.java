package examples.example3_annotation;

import com.github.drinkjava2.BeanBox;

public class TesterBox extends BeanBox {
	{
		this.setClassOrValue(Tester.class);
		this.setProperty("s8", "Hello8");// //case 8
		this.setProperty("s9", "Hello9");// //case 8
	}

	public static class StringBox extends BeanBox {// case 5
		{
			this.setClassOrValue("Hello5");
		}
	}
}
