package examples.example8_benchmark.objects;

import java.util.Objects;

import net.sf.jbeanbox.InjectBox;

public class A {
	public B b;

	@InjectBox(box0 = B.class)
	public A(B b) {
		this.b = b;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		A a = (A) o;
		return Objects.equals(b, a.b);
	}

	@Override
	public int hashCode() {
		return Objects.hash(b);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
