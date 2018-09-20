package com.github.drinkjava2.jbeanbox; 
public class AopUtils { 
public static Object createProxyBean(Class<?> clazz, BeanBox box, BeanBoxContext ctx) { 
	BeanBoxException.throwEX("jBeanBoxDI does not support AOP."); 
	return null;
}
}
