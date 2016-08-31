package examples.example8_benchmark.objects;

public class E {

	public String getname() {
		return this.getClass().getName();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
