package com.github.drinkjava2.jbeanbox;

import static com.github.drinkjava2.jbeanbox.JBEANBOX.autowired;
import static com.github.drinkjava2.jbeanbox.JBEANBOX.value;

import javax.annotation.PreDestroy;

import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.github.drinkjava2.jbeanbox.annotation.VALUE;

/**
 * BeanBoxTest
 *
 * @author Yong Zhu
 * @since 2.4.7
 */
//@formatter:off
public class HelloWorld {
	public static class User {
		String name;
		
		public User() {}
		
		@VALUE("User1")
		public User(String name) {	this.name = name;}
		
		void setName(String name) {	this.name = name;}
		
		void init() {this.name = "User6";}
		
		@PreDestroy
		void end() {this.name= "User10";}
	}

	public static class UserBox extends BeanBox {
		public Object create() {return new User("User2");}
	}
	
	public static class UserBox7 extends BeanBox {
		{   setBeanClass(User.class);
			setProperty("name", "User7");
		} 
	}

	public static class H8 extends UserBox {{setAsValue("User8");}}
 
	public static void main(String[] args) {
		User u1 = JBEANBOX.getInstance(User.class);
		User u2 = JBEANBOX.getBean(UserBox.class);
		User u3 = JBEANBOX.getBean(new BeanBox().injectConstruct(User.class, String.class, value("User3")));
		User u4 = JBEANBOX.getBean(new BeanBox(User.class).injectValue("name", "User4" ));
		User u5 = JBEANBOX
				.getBean(new BeanBox(User.class).injectMethod("setName", String.class, value("User5")));
		User u6 = JBEANBOX.getBean(new BeanBox().setBeanClass(User.class).setPostConstruct("init"));
		User u7 = new UserBox7().getBean();
		
		BeanBoxContext ctx = new BeanBoxContext(); 
		Interceptor aop=new MethodInterceptor() { 
			public Object invoke(MethodInvocation invocation) throws Throwable { 
				invocation.getArguments()[0]="User9";
				return invocation.proceed();
			}
		};
		User u8 = ctx.rebind(String.class, "8").bind("8", H8.class)
				.getBean(ctx.getBeanBox(User.class).addMethodAop(aop, "setName",String.class).injectField("name", autowired())); 
		System.out.println(u1.name); //Result: User1
		System.out.println(u2.name); //Result: User2
		System.out.println(u3.name); //Result: User3
		System.out.println(u4.name); //Result: User4
		System.out.println(u5.name); //Result: User5
		System.out.println(u6.name); //Result: User6
		System.out.println(u7.name); //Result: User7
		System.out.println(u8.name); //Result: User8
		u8.setName("");
		System.out.println(u8.name); //Result: User9
		ctx.close();
		System.out.println(u8.name); //Result: User10  
	}
}