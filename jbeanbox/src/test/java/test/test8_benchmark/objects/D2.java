package test.test8_benchmark.objects;

import com.github.drinkjava2.jbeanbox.InjectBox;

@InjectBox(prototype = true)
public class D2 {
	@SuppressWarnings("unused")
	private final E e;

	@InjectBox
	public D2(E e) {
		this.e = e;

	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
