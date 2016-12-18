package test.core_test.circular_dependency;

import org.junit.Test;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.BeanBoxException;
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

	@Test(expected = BeanBoxException.class)
	public void doTest() {
		BeanBox.getBean(ClassA.class);
	}

	public static void main(String[] args) {
		BeanBox.getBean(ClassA.class);
	}
}
