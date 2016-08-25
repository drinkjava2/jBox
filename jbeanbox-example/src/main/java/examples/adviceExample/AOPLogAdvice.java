package examples.adviceExample;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * An example of AOP alliance Advice
 * 
 * @author Yong Zhu
 * @since 2016-2-29
 * @update 2016-6-18
 */
public class AOPLogAdvice {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object doAround(MethodInvocation caller) throws Throwable {
		System.out.println("=====logger " + name + ", doAround method begin: " + caller.getMethod().getName() + "=====");
		Object o = caller.proceed();
		System.out.println("=====logger " + name + ", doAround method end: " + caller.getMethod().getName() + "=====");
		return o;
	}

	public void doBefore(Method method, Object[] args, Object target) throws Throwable {
		System.out.println("=====logger " + name + ", doBefore method: " + method.getName() + "======");
	}

	public void doAfterReturning(Object result, Method method, Object[] args, Object target) throws Throwable {
		System.out.println("=====logger " + name + ", doAfterReturning method: " + method.getName() + "======");
	}

	public void doAfterThrowing(Method method, Object[] args, Object target, Exception ex) {
		System.out.println("=====logger " + name + ", doAfterThrowing method: " + method.getName() + "======");
	}

}