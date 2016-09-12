package examples.example8_benchmark.objects;

import com.github.drinkjava2.InjectBox;

@InjectBox(prototype = true)
public class C {
	public D1 d1;
	public D2 d2;

	@InjectBox
	public C(D1 d1, D2 d2) {
		this.d1 = d1;
		this.d2 = d2;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
