package test.core_test.circular_dependency;

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
		@InjectBox
		public ClassA(ClassB b) {
		}
	}

	public static class ClassB {
		@InjectBox
		public ClassB(ClassA a) {
		}
	}

	@Test
	public void doTest() {
		BeanBox.getBean(ClassA.class);
	}

	public static void main(String[] args) {
		BeanBox.getBean(ClassA.class);
	}
}
