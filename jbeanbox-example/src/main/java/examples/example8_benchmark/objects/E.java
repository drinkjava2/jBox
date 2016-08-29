package examples.example8_benchmark.objects;

import java.util.Objects;

public class E {

	@Override
	public int hashCode() {
		return Objects.hashCode("e");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return (obj instanceof E);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
