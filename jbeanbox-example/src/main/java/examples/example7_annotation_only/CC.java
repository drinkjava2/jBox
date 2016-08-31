package examples.example7_annotation_only;

import net.sf.jbeanbox.InjectBox;

public class CC {
	String name;
	DD d1;
	DD d2;

	@InjectBox(s0 = "Jerry", box1 = DD.class, box2 = DD.class)
	public CC(String name, DD d1, DD d2) {
		System.out.println("CC inject name is " + name);
		this.d1 = d1;
		this.d2 = d2;
	}
}
