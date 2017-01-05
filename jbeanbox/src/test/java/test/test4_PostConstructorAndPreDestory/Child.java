package test.test4_PostConstructorAndPreDestory;

import com.github.drinkjava2.BeanBox;

/**
 * @author Yong
 * @since 2.4
 */
public class Child extends Parent {
	public static class ChildBox extends BeanBox {
		{
			setPostConstructor("init");
			setPreDestory("destory");
		}
	}
}