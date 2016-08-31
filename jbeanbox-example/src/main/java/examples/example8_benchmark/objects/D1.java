package examples.example8_benchmark.objects;

import net.sf.jbeanbox.InjectBox;

public class D1 {
	public E e;

	@InjectBox(box0 = E.class)
	public D1(E e) {
		this.e = e;
	}
 
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
