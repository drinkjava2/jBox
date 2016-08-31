package examples.example8_benchmark.objects;

import net.sf.jbeanbox.InjectBox;

public class D2 {
	@SuppressWarnings("unused")
	private final E e;

	@InjectBox(box0 = E.class)
	public D2(E e) {
		this.e = e;

	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
