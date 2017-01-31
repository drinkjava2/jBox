package test.core_test.circular_dependency;

import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jbeanbox.InjectBox;

/**
 * Circular Dependency internal Test
 */
/**
 * @author Yong Zhu
 * @since 2.4
 *
 */
public class CircularDependencyTest {
	@Before
	public void beforeTest() {
		System.out.println("========= Circular Dependency Exception Test =========");
	}

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
