package examples.example8_benchmark.objects;

import java.util.Objects;

import net.sf.jbeanbox.InjectBox;

public class C {
	public D1 d1;
	public D2 d2;

	@InjectBox(box0 = D1.class, box1 = D2.class)
	public C(D1 d1, D2 d2) {
		this.d1 = d1;
		this.d2 = d2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		C c = (C) o;
		return Objects.equals(d1, c.d1) && Objects.equals(d2, c.d2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(d1, d2);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
