package test.test2_aop;

/**
 *
 *
 * @author Yong Zhu
 * @since 2.4
 */
public class ItemImpl implements Iitem {
	public void doPrint() {
		System.out.println("Hello AOP!");
		// int i = 1 / 0;
	};

}