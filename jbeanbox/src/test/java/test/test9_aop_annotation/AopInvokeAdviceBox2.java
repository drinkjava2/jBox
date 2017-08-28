package test.test9_aop_annotation;

import com.github.drinkjava2.jbeanbox.BeanBox;

import test.logger_advice.AopInvokeAdvice;

/**
 * An example of AOP alliance Advice
 * 
 * @author Yong Zhu
 * @since 2.4
 */
public class AopInvokeAdviceBox2 extends BeanBox {
	{
		this.setClassOrValue(AopInvokeAdvice.class);
		this.setProperty("name", "Jerry");
	}
}