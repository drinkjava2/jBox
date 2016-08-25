package examples.example3_annotation;

import net.sf.jbeanbox.BeanBox;
import net.sf.jbeanbox.BeanBoxContext;
import net.sf.jbeanbox.InjectBox;

/**
 * 
 * Example of different field injections, 1 to 7 are "pull" type injection, 8&9 are "push" type injection, from this example we can see Annotation make source code concise, but hard to understand and
 * maintenance.
 * 
 * @author Yong
 */
public class Tester {
	@InjectBox(A.StrBox.class)
	String s1;// Use StrBox.class, recommend

	@InjectBox(A.class)
	String s2;// Use A.StringBox.class (or A.StringBox2.class, 2 to 8 depends context setting)

	@InjectBox(B.class)
	String s3;// Use B$S3Box.class

	@InjectBox
	C c4;// Use CBox.class, recommend

	@InjectBox
	String s5;// Use TesterBox$StringBox.class

	@InjectBox(required = false)
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
		System.out.println(this);
	}

	public static void main(String[] args) {
		Tester t = BeanBox.getBean(Tester.class);
		t.print();
		BeanBox.defaultBeanBoxContext.close();
		System.out.println();

		BeanBox.defaultBeanBoxContext.addConfig(Config.class);
		Tester t2 = new TesterBox().getBean();
		t2.print();
		BeanBox.defaultBeanBoxContext.close();
		System.out.println();

		BeanBoxContext ctx = new BeanBoxContext(Config2.class).setBoxIdentity("Box2");
		Tester t3 = ctx.getBean(Tester.class);
		t3.print();
		System.out.println(t3 == new TesterBox2().setContext(ctx).getBean());//true
		ctx.close();
	}
}