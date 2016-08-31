package examples.example8_benchmark.objects;

import net.sf.jbeanbox.InjectBox;

public class B {
	public C c;

	@InjectBox(box0 = C.class)
	public B(C c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
