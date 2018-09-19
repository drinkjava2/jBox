XCOPY "..\jbeanbox\src\main\java\javax\*.*" ".\src\main\java\javax\" /S /D /Y
XCOPY "..\jbeanbox\src\main\java\org\springframework\*.*" ".\src\main\java\org\springframework\" /S /D /Y
XCOPY "..\jbeanbox\src\main\java\com\github\drinkjava2\jbeanbox\*.*" ".\src\main\java\com\github\drinkjava2\jbeanbox\" /S /D /Y
XCOPY "..\jbeanbox\src\test\*.*" ".\src\test\" /S /D /Y
del ".\src\test\java\com\github\drinkjava2\jbeanbox\aop\*.java"
rd ".\src\test\java\com\github\drinkjava2\jbeanbox\aop"

del ".\src\main\java\com\github\drinkjava2\jbeanbox\AopUtils.java"
del ".\src\main\java\com\github\drinkjava2\jbeanbox\ProxyBean.java" 


set aop=.\src\main\java\com\github\drinkjava2\jbeanbox\AopUtils.java
 
@echo package com.github.drinkjava2.jbeanbox; >%aop%
@echo public class AopUtils { >>%aop%
@echo	public static Object createProxyBean(Class^<^?^> clazz, BeanBox box, BeanBoxContext ctx) { >>%aop%
@echo		BeanBoxException.throwEX("jBeanBoxDI does not support AOP."); >>%aop%
@echo		return null;>>%aop%
@echo	}>>%aop%
@echo }>>%aop%
