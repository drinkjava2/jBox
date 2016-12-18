package test.test4_PostConstructorAndPreDestory;

import com.github.drinkjava2.BeanBox;

/**
 * @author Yong
 */
public class Child extends Parent {
	public static class ChildBox extends BeanBox {
		{
			setPostConstructor("init");
			setPreDestory("destory");
		}
	}
}