package test.test7_annotation2;

import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.InjectBox;

/**
 * This example shows constructor & method parameters injection:<br/>
 * 
 * 
 * @author Yong Zhu
 * @since 2.4
 */
public class TesterA {
	@Before
	public void beforeTest() {
		System.out.println("========= Constructor & method parameters injection test =========");
	}

	public static class Tester {
		String name1;

		String name2;

		@InjectBox(s = "name3")
		String name3;

		AA a4, a5;

		@InjectBox(s1 = "name1")
		public Tester(String name1, AA a4) {// a4 automatically find AABox
			this.name1 = name1;
			this.a4 = a4;
		}

		@InjectBox(s1 = "name2", box2 = A5Box.class)
		public void injectBymethod(String name2, AA a5) {
			this.name2 = name2;
			this.a5 = a5;
		}

		public static class AA {
			public String name;
		}

		public static class AABox extends BeanBox {
			{
				this.setProperty("name", "name4");
			}
		}

		public static class A5Box extends BeanBox {
			public AA create() {
				AA aa = new AA();
				aa.name = "name5";
				return aa;
			}
		}
	}

	@Test
	public void testIt() {
		Tester t = BeanBox.getBean(Tester.class);
		System.out.println("name1=" + t.name1); // name1=name1
		System.out.println("name2=" + t.name2); // name2=name2
		System.out.println("name3=" + t.name3); // name3=name3
		System.out.println("name4=" + t.a4.name); // name4=name4
		System.out.println("name5=" + t.a5.name); // name5=name5
	}

}