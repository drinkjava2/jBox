package test;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.BeanBox;

public class HelloWorld {
	private String field1;

	public static class HelloWorldBox extends BeanBox {
		{
			this.setProperty("field1", "Hello World!");
		}
	}

	@Test
	public void doTest() {
		HelloWorld h = BeanBox.getBean(HelloWorld.class);
		Assert.assertEquals("Hello World!", h.field1);
	}

	public static void main(String[] args) {
		HelloWorld h = BeanBox.getBean(HelloWorld.class);
		System.out.println(h.field1); // print "Hello World!"
	}
}