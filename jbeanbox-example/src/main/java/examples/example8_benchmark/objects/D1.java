package examples.example8_benchmark.objects;

import java.util.Objects;

public class D1 {

	public D1(E e) {
	}

	@Override
	public int hashCode() {
		return Objects.hash("d1");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return (obj instanceof D1);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
