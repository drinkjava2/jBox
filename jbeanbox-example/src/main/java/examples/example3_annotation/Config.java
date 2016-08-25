package examples.example3_annotation;

import net.sf.jbeanbox.BeanBox;

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
