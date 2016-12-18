package test.test8_benchmark.objects;

import com.github.drinkjava2.InjectBox;

@InjectBox(prototype = true)
public class B {
	public C c;

	@InjectBox
	public B(C c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
