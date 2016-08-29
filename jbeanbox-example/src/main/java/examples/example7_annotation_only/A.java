package examples.example7_annotation_only;

import net.sf.jbeanbox.InjectBox;

public class A {
	@InjectBox(s1 = "", s2 = "abc")
	public A(String s1, String s2) {
		System.out.println("s1=" + s1 + " s2=" + s2);
	}
}
