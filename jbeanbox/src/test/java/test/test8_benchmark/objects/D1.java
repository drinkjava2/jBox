package test.test8_benchmark.objects;

import com.github.drinkjava2.InjectBox;

@InjectBox(prototype = true)
public class D1 {
	public E e;

	@InjectBox
	public D1(E e) {
		this.e = e;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
