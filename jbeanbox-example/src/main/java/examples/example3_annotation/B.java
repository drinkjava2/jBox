package examples.example3_annotation;

import net.sf.jbeanbox.BeanBox;

public class B {
	public static class S3Box extends BeanBox {
		{
			this.setClassOrValue("Hello3");
		}
	}
	
	public static class S3Box2 extends BeanBox {
		{
			this.setClassOrValue("Hi 3");
		}
	}
}
