版本发布记录  
2.4.1 第一版发布，仅支持Java7或以上版本  
2.4.2 增加一个@AopAround注释并更正一些Bug    
2.4.3 删除不常用的AspectJ支持，去除对Aop接口的依赖，将AOP接口以源码内嵌方式包含在项目内，从Java7降级到Java6发布，以支持更多开发环境。 
2.4.4 改正@AopAround不支持多个方法的Bug
2.4.5 改正AOP不支持构数函数带参数的Bug
      添加setConstructorTypes方法，用于明确指定构造器参数类型,消除岐义，通常用于实参为子类的情况。
      改正内部类Advice失效Bug 	  

Release History
2.4.1 First version, support Java7 only  
2.4.2 Add a @AopAround annotation  
2.4.3 Remove AspectJ support because it's not common used  
      Direct include AOP alliance interface source code into project   
      Downgrade from Java7 to Java6 to support older developing environment  
2.4.4 Fix @AopAround does not support multiple methods bug  
2.4.5 Fix AOP proxy do not support constructor parameters bug  
      Add beanBox.setConstructorTypes() method, otherwise if constructor parameter is a subClass will have a Exception "Can not find constructor"  
	  Fix inner class advice bug
	  
