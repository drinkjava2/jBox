package test;

import org.junit.Test;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.InjectBox;

/**
 * Circular Dependency internal Test
 */
/**
 * @author Yong Zhu
 * @since 2.4
 *
 */
public class CircularDependencyTest {

	public static class ClassA {
		public ClassA(ClassB b) {

		}
	}

	public static class ClassB {
		@InjectBox
		public ClassB(ClassA a) {
		}
	}

	public static class ClassABox extends BeanBox {
		{
			this.setConstructor(ClassA.class, ClassB.class);
		}
	}

	public static void main(String[] args) {
		// uncomment below lines will cause a circular dependency exception
		// System.out.println("\r\n============== CircularDependencyTest===================");
		// BeanBox.getBean(ClassA.class);
	}

	@Test
	public void testMain() {
		main(null);
	}

}
