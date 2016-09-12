package examples.example8_benchmark.objects;

import com.github.drinkjava2.InjectBox;

@InjectBox(prototype = true)
public class E {

	public String getname() {
		return "" + this;
	}

}
