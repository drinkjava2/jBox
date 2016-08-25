package examples.example2_aop;

import net.sf.jbeanbox.BeanBox;
import examples.adviceExample.AOPLogAdvice;
import examples.adviceExample.AspectjLogAdvice;

/**
 * @author Yong Zhu
 * @since Mar 1, 2016
 */
public class Tester {
	private Iitem item;

	public void setItem(Iitem item) {
		this.item = item;
	}

	public void doPrintItem() {
		item.doPrint();
	}

	public static void main(String[] args) {
		BeanBox advice = new BeanBox(AOPLogAdvice.class).setProperty("name", "AOP Logger");
		BeanBox.defaultBeanBoxContext.setAOPAround("examples.example2_aop.\\w*", "doPrint\\w*", advice, "doAround");
		BeanBox.defaultBeanBoxContext.setAOPBefore("examples.example2_aop.\\w*", "doPrint\\w*", advice, "doBefore");
		BeanBox.defaultBeanBoxContext.setAOPAfterReturning("examples.example2_aop.\\w*", "doPrint\\w*", advice, "doAfterReturning");
		BeanBox.defaultBeanBoxContext.setAOPAfterThrowing("examples.example2_aop.\\w*", "doPrint\\w*", advice, "doAfterThrowing");

		BeanBox advice2 = new BeanBox(AspectjLogAdvice.class).setProperty("name", "AspectJ Logger");
		BeanBox.defaultBeanBoxContext.setAspectjAround("examples.example2_aop.\\w*", "doPrint\\w*", advice2, "doAround");
		BeanBox.defaultBeanBoxContext.setAspectjBefore("examples.example2_aop.\\w*", "doPrint\\w*", advice2, "doBefore");
		BeanBox.defaultBeanBoxContext.setAspectjAfterReturning("examples.example2_aop.\\w*", "doPrint\\w*", advice2, "doAfterReturning");
		BeanBox.defaultBeanBoxContext.setAspectjAfterThrowing("examples.example2_aop.\\w*", "doPrint\\w*", advice2, "doAfterThrowing");

		Tester t = new BeanBox(Tester.class) {
			{
				this.setProperty("item", ItemImpl.class);
			}
		}.getBean();
		t.doPrintItem();
	}
}
