package test.logger_advice;

import org.aopalliance.intercept.MethodInvocation;

import com.github.drinkjava2.jbeanbox.BeanBox;

/**
 * An example of AOP alliance Advice
 * 
 * @author Yong Zhu
 * @since 2.4
 */
public class AopInvokeAdvice {
	public String name;

	public Object invoke(MethodInvocation caller) throws Throwable {
		System.out.println("====" + name + " invoke method begin: " + caller.getMethod().getName() + "=====");
		Object o = caller.proceed();
		System.out.println("====" + name + " invoke method end: " + caller.getMethod().getName() + "=====");
		return o;
	}

	public static class AopInvokeAdviceBox extends BeanBox {
		{
			this.setClassOrValue(AopInvokeAdvice.class);
			this.setProperty("name", "Tom");
		}
	}

}