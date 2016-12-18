package test.test2_aop.advices;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * An example of AspectJ Advice
 * 
 * @author Yong Zhu
 * @since 2016-6-18
 * @update 2016-6-18
 */
public class AspectjLogAdvice {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("=====logger " + name + ", doAround method begin: " + pjp.getSignature().getName() + "=====");
		Object o = pjp.proceed();
		System.out.println("=====logger " + name + ", doAround method end: " + pjp.getSignature().getName() + "=====");
		return o;
	}

	public void doBefore(JoinPoint jp) throws Throwable {
		System.out.println("=====logger " + name + ", doBefore method:" + jp.getSignature().getName() + "======");
	}

	public void doAfterReturning(JoinPoint jp, Object result) throws Throwable {
		System.out.println("=====logger " + name + ", doAfterReturning method: " + jp.getSignature().getName() + "======");
	}

	public void doAfterThrowing(JoinPoint jp, Exception ex) throws Throwable {
		System.out.println("=====logger " + name + ", doAfterThrowing method: " + jp.getSignature().getName() + "======");
	}

}
