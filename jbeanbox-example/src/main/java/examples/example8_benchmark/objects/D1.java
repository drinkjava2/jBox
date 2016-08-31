package examples.example8_benchmark.objects;

import java.util.Objects;

import net.sf.jbeanbox.InjectBox;

public class D1 {
	public E e;

	@InjectBox(box0 = E.class)
	public D1(E e) {
		this.e = e;
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
