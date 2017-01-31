
package test.core_test.reflection_utils;

import com.github.drinkjava2.jbeanbox.BeanBox;

/**
 *
 *
 * @author Yong Zhu
 *
 * @since 2.4
 */
public class CreateMethodTest {

	public static class BaseClassBox extends BeanBox {
		Parent create() {
			Parent p = new Parent();
			return p;
		}
	}

	public static class childClassBox extends BaseClassBox {
		{
			this.setProperty("userName", "u1");
		}
	}

	public static void main(String[] args) {
		Parent p = BeanBox.getBean(childClassBox.class);
		System.out.println(p);
		System.out.println(p.getUserName());
	}

}