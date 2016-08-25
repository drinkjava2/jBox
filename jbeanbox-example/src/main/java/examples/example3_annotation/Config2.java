package examples.example3_annotation;

import net.sf.jbeanbox.BeanBox;

public class Config2 {
	public static class DBox2 extends BeanBox {
		{
			this.setProperty("value", "Hi 6");
		}
	}

	public static class E7Box2 extends BeanBox {
		{
			this.setProperty("value", "Hi 7");
		}
	}
}
