package com.github.drinkjava2.jbeanbox;

import static com.github.drinkjava2.jbeanbox.JBEANBOX.*;
import static com.github.drinkjava2.jbeanbox.JBEANBOX.cons;

import com.github.drinkjava2.jbeanbox.annotation.CONS;

/**
 * BeanBoxTest
 *
 * @author Yong Zhu
 * @since 2.4.7
 */
public class HelloWorld {

	public static class Hello {
		String name;

		public Hello() {
		}

		@CONS("Hello1")
		public Hello(String name) {
			this.name = name;
		}

		void setName(String name) {
			this.name = name;
		}

		void init() {
			this.name = "Hello6";
		}
	}

	public static class HelloBox extends BeanBox {
		Object create() {
			return new Hello("Hello2");
		}
	}

	public static class H5 extends BeanBox {
		{
			setAsConstant("Hello5");
		}
	}

	public static void main(String[] args) {
		System.out.println(JBEANBOX.getInstance(Hello.class).name);

		JBEANBOX.reset();
		Hello h2 = JBEANBOX.getBean(HelloBox.class);
		System.out.println(h2.name);

		JBEANBOX.reset();
		Hello h3 = JBEANBOX.getBean(new BeanBox().injectConstruct(Hello.class, String.class, cons("Hello3")));
		System.out.println(h3.name);

		JBEANBOX.reset();
		Hello h4 = JBEANBOX
				.getBean(new BeanBox().setBeanClass(Hello.class).injectMethod("setName", String.class, cons("Hello4")));
		System.out.println(h4.name);

		JBEANBOX.reset();
		JBEANBOX.bind(String.class, "5").bind("5", H5.class);
		Hello h5 = JBEANBOX.getBean(getBox(Hello.class).injectField("name", autowired()));
		System.out.println(h5.name);

		JBEANBOX.reset();
		Hello h6 = JBEANBOX.getBean(new BeanBox().setSingleton(true).setBeanClass(Hello.class).setPostConstruct("init")
				.setPreDestroy("close"));
		System.out.println(h6.name);
	}
}