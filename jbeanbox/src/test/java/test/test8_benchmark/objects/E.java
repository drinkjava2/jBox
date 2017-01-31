package test.test8_benchmark.objects;

import com.github.drinkjava2.jbeanbox.InjectBox;

@InjectBox(prototype = true)
public class E {

	public String getname() {
		return "" + this;
	}

}
