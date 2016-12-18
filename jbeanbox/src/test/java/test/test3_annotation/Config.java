package test.test3_annotation;

import com.github.drinkjava2.BeanBox;

public class Config {
	public static class DBox extends BeanBox {
		{
			this.setProperty("value", "Hello6");
		}
	}
	
	public static class E7Box extends BeanBox {
		{
			this.setProperty("value", "Hello7");
		}
	}
	
	
}
