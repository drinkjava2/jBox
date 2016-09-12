package examples.example2_aop;

import com.github.drinkjava2.BeanBox;

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
		BeanBox.defaultContext.setAOPAround("examples.example2_aop.\\w*", "doPrint\\w*", advice, "doAround");
		BeanBox.defaultContext.setAOPBefore("examples.example2_aop.\\w*", "doPrint\\w*", advice, "doBefore");
		BeanBox.defaultContext.setAOPAfterReturning("examples.example2_aop.\\w*", "doPrint\\w*", advice,
				"doAfterReturning");
		BeanBox.defaultContext.setAOPAfterThrowing("examples.example2_aop.\\w*", "doPrint\\w*", advice,
				"doAfterThrowing");

		BeanBox advice2 = new BeanBox(AspectjLogAdvice.class).setProperty("name", "AspectJ Logger");
		BeanBox.defaultContext.setAspectjAround("examples.example2_aop.\\w*", "doPrint\\w*", advice2, "doAround");
		BeanBox.defaultContext.setAspectjBefore("examples.example2_aop.\\w*", "doPrint\\w*", advice2, "doBefore");
		BeanBox.defaultContext.setAspectjAfterReturning("examples.example2_aop.\\w*", "doPrint\\w*", advice2,
				"doAfterReturning");
		BeanBox.defaultContext.setAspectjAfterThrowing("examples.example2_aop.\\w*", "doPrint\\w*", advice2,
				"doAfterThrowing");

		Tester t = new BeanBox(Tester.class) {
		}.setProperty("item", ItemImpl.class).getBean();
		t.doPrintItem();
	}
}
