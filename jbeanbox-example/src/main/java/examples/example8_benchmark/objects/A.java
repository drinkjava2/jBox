package examples.example8_benchmark.objects;

import net.sf.jbeanbox.InjectBox;

public class A {
	public B b;

	@InjectBox
	public A(B b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
