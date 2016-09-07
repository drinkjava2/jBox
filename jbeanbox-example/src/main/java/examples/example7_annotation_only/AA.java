package examples.example7_annotation_only;

import net.sf.jbeanbox.BeanBox;
import net.sf.jbeanbox.InjectBox;

public class AA {
	BB bb;
	String s;
	BB bb2;
	CC cc1;
	Integer i;
	boolean bl;
	String name;

	@InjectBox(box0 = BB.class, s1 = "Hello", i3 = 12345, b4 = "false")
	public AA(BB bb, String s, BB bb2, Integer i, Boolean bl) {
		this.bb = bb;
		this.s = s;
		this.bb2 = bb2;
		this.i = i;
		this.bl = bl;
	}

	public static class AABox extends BeanBox {
		{
			this.setProperty("cc1", CC.class);
		}

		public void config(AA aa) {
			aa.name = "aa";
		}
	}

	void print() {
		System.out.println("s=" + s);
		System.out.println("i=" + i);
		System.out.println("bl=" + bl);
		System.out.println(bb.cc.d1.aa.bb.cc.d1.aa.bb.cc.d1.name);
		System.out.println(bb.cc.d2.name);
		System.out.println(cc1.d2.name);
		System.out.println(name);
	}

}
