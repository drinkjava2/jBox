package examples.example3_annotation;

import net.sf.jbeanbox.BeanBox;

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
