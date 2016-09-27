package examples.example8_benchmark.objects;

import com.github.drinkjava2.InjectBox;

@InjectBox(prototype = true)
public class A {
	public B b;

	@InjectBox
	public A(B b) {
		this.b = b;
	}

	// @Override
	// public String toString() {
	// return getClass().getSimpleName();
	// }

}
