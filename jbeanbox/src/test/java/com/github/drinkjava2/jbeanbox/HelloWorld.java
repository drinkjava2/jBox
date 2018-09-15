package com.github.drinkjava2.jbeanbox;

import static com.github.drinkjava2.jbeanbox.JBEANBOX.autowired;
import static com.github.drinkjava2.jbeanbox.JBEANBOX.cons;

import com.github.drinkjava2.jbeanbox.annotation.CONST;

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

		@CONST("Hello1")
		public Hello(String name) {
			this.name = name;
		}

		void setName(String name) {
			this.name = name;
		}

		void init() {
			this.name = "Hello5";
		}
	}

	public static class HelloBox extends BeanBox {
		Object create() {
			return new Hello("Hello2");
		}
	}

	public static class H6 extends HelloBox {
		{
			setAsConstant("Hello6");
		}
	}

	public static void main(String[] args) {
		System.out.println(JBEANBOX.getInstance(Hello.class).name);

		Hello h2 = JBEANBOX.bind(Hello.class, HelloBox.class).getBean(Hello.class);
		System.out.println(h2.name);

		Hello h3 = JBEANBOX.getBean(new BeanBox().injectConstruct(Hello.class, String.class, cons("Hello3")));
		System.out.println(h3.name);

		Hello h4 = JBEANBOX
				.getBean(new BeanBox().setBeanClass(Hello.class).injectMethod("setName", String.class, cons("Hello4")));
		System.out.println(h4.name);

		Hello h5 = JBEANBOX.getBean(new BeanBox().setBeanClass(Hello.class).setPostConstruct("init"));
		System.out.println(h5.name);

		BeanBoxContext ctx = new BeanBoxContext();
		Hello h6 = ctx.bind(String.class, "6").bind("6", H6.class)
				.getBean(ctx.getBeanBox(Hello.class).injectField("name", autowired()));
		System.out.println(h6.name);
	}
}