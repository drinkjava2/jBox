package examples.example3_annotation;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.BeanBoxContext;
import com.github.drinkjava2.BeanBoxUtils;
import com.github.drinkjava2.InjectBox;

/**
 * 
 * Example of different field injections, 1 to 7 are "pull" type injection, 8&9 are "push" type injection, from this
 * example we can see Annotation make source code concise, but hard to understand and maintenance.
 * 
 * @author Yong
 */
public class Tester {
	@InjectBox(A.StrBox.class)
	String s1;// Use StrBox.class, recommend

	@InjectBox(A.class)
	String s2;// Use A.StringBox.class (or A.StringBox2.class, 2 to 8 depends context setting)

	@InjectBox(value = B.class, required = false)
	String s3;// Use B$S3Box.class (or B$S3Box2.class)

	@InjectBox
	C c4;// Use CBox.class (or CBox2.class) , recommend

	@InjectBox
	String s5;// Use TesterBox$StringBox.class or (TesterBox2$StringBox2.class)

	@InjectBox(required = false) // default is false, if set to true and no BeanBox found, throw error
	D d6;// Use Config$DBox.class (or Config2$DBox2)

	@InjectBox(required = false)
	E e7;// Use Config$E7Box.class (or Config2$E7Box2)

	private String s8; // injected by field, not suitable for Proxy bean

	private String s9; // injected by setter method, recommend

	public void setS9(String s9) {
		this.s9 = s9;
	}

	public void print() {
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3);
		System.out.println((c4 == null) ? null : c4.value);
		System.out.println(s5);
		System.out.println((d6 == null) ? null : d6.value);
		System.out.println((e7 == null) ? null : e7.value);
		System.out.println(s8);
		System.out.println(s9);
	}

	public void setValueManually(BeanBoxContext ctx) {// internal test
		s1 = ctx.getBean(A.StrBox.class);
		s2 = BeanBoxUtils.getBeanBox(null, String.class, A.class, null, ctx, false).getBean();
		s3 = BeanBoxUtils.getBeanBox(null, String.class, B.class, "s3", ctx, false).getBean();
		c4 = ctx.getBean(C.class);
		s5 = BeanBoxUtils.getBeanBox(this.getClass(), String.class, null, null, ctx, false).getBean();
		d6 = ctx.getBean(D.class);
		e7 = BeanBoxUtils.getBeanBox(null, E.class, null, "e7", ctx, false).getBean();
	}

	public static void main(String[] args) {
		System.out.println("\r\n==Teseter3.1==");
		Tester t = BeanBox.getBean(Tester.class);
		t.print();
		BeanBox.defaultContext.close();

		System.out.println("\r\n==Teseter3.2==");
		BeanBox.defaultContext.addConfig(Config.class);
		Tester t2 = new TesterBox().getBean();
		t2.print();
		BeanBox.defaultContext.close();

		System.out.println("\r\n==Teseter3.3==");
		BeanBoxContext ctx = new BeanBoxContext(Config2.class).setBoxIdentity("Box2");
		Tester t3 = ctx.getBean(Tester.class);
		t3.print();
		System.out.println(t3 == new TesterBox2().setContext(ctx).getBean());// true
		BeanBox.defaultContext.close();

		// This one is test getBean method
		System.out.println("\r\n==Teseter3.4==");
		Tester t4 = new Tester();
		t4.setValueManually(BeanBox.defaultContext.addConfig(Config.class));
		t4.print();
		BeanBox.defaultContext.close();

		System.out.println("\r\n==Teseter3.5==");
		t4.setValueManually(BeanBox.defaultContext.addConfig(Config2.class).setBoxIdentity("Box2"));
		t4.print();
		BeanBox.defaultContext.close();

	}
}