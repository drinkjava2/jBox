package examples.example7_annotation_only;

import net.sf.jbeanbox.InjectBox;

public class DD {
	String name = "Tom";

	@InjectBox
	AA aa;

	public void print() {
		System.out.println("DD name is " + name);
	}

}
