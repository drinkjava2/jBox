package examples.example8_benchmark.objects;

import com.github.drinkjava2.InjectBox;

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
