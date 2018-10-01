# jBeanBox 
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  
(注：中文介绍请见[README-CH.md](README-CH.md) )  

jBeanBox is a micro-shaped but full-featured IOC/AOP tool. In addition to the imported third-party library, its core has only a dozen classes, and the source code is only about 1500 lines. It uses the "Box" programming model, using pure Java classes as a configuration. jBeanBox runs on JDK 1.6 or above.  
The purpose of jBeanBox development is to overcome some of the problems of other IOC/AOP tools:  
1. Spring: The source code is bloated, the Java mode is not flexible, and there are problems in dynamic configuration, configuration inheritance, slow start, and non-single-mode mode.  
2. Guice: The source code is slightly bloated (200 classes), it is not very convenient to use, and the life cycle support of Bean is not good.  
3. Feather: The source code is minimal (several hundred lines), but it is not fully functional. It is just a DI tool and does not support AOP.  
4. Dagger: The source code is slightly bloated (300 classes), static injection during compile time, slightly inconvenient to use, does not support AOP.  
5. Genie: This is the kernel of ActFramework, just a DI tool, not support AOP.  

### How to use jBeanBox?  
Manually download jbeanbox-2.4.8.jar put into the project's class path, or add the following configuration to pom.xml:
```
<dependency>
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jbeanbox</artifactId>
    <version>2.4.8</version> <!--Or newest-->
</dependency>
``` 
jBeanBox does not depend on any third-party libraries. To avoid package conflicts, third-party libraries such as CGLIB that it uses are included in jBeanBox by source code.  
jBeanBox jar size is large, about 750K, if you do not need AOP feature, you can only use its DI kernel, called "jBeanBoxDI", only 49k size, put below in pom.xml:
```
<dependency>
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jbeanboxdi</artifactId>
    <version>2.4.8</version> <!--Or newest-->
</dependency>
``` 

### First jBeanBox demo：  
The demo shows 9 different injection methods:  
```
public class HelloWorld {
  public static class User {
    String name;    
    
    public User() {   }    
    
    @VALUE("User1")  
    public User(String name) { this.name = name; }   
    
    void setName(String name) { this.name = name; } 
    
    void init() {this.name = "User6";}    
    
    @PreDestroy 
    void end() {this.name= "User9";}
  }

  public static class UserBox extends BeanBox {
     Object create() {return new User("User2");}
  }

  public static class H7 extends UserBox {{setAsValue("User7");}}
 
  public static void main(String[] args) {
    User u1 = JBEANBOX.getInstance(User.class);
    User u2 = JBEANBOX.getBean(UserBox.class);
    User u3 = JBEANBOX.getBean(new BeanBox().injectConstruct(User.class, String.class, value("User3")));
    User u4 = JBEANBOX.getBean(new BeanBox(User.class).injectValue("name", "User4" ));
    User u5 = JBEANBOX
        .getBean(new BeanBox(User.class).injectMethod("setName", String.class, value("User5")));
    User u6 = JBEANBOX.getBean(new BeanBox().setBeanClass(User.class).setPostConstruct("init"));
    
    BeanBoxContext ctx = new BeanBoxContext(); 
    Interceptor aop=new MethodInterceptor() { 
      public Object invoke(MethodInvocation invocation) throws Throwable { 
        invocation.getArguments()[0]="User8";
        return invocation.proceed();
      }
    };
    User u7 = ctx.bind(String.class, "7").bind("7", H7.class)
      .getBean(ctx.getBeanBox(User.class).addMethodAop(aop, "setName",String.class).injectField("name", autowired())); 
    System.out.println(u1.name); //Result: User1
    System.out.println(u2.name); //Result: User2
    System.out.println(u3.name); //Result: User3
    System.out.println(u4.name); //Result: User4
    System.out.println(u5.name); //Result: User5
    System.out.println(u6.name); //Result: User6
    System.out.println(u7.name); //Result: User7
    u7.setName("");
    System.out.println(u7.name); //Result: User8
    ctx.close();
    System.out.println(u7.name); //Result: User9 
  }
} 
```
The output of this example is to print out "User1", "User2"... to "User9" in sequence. Here is explanation:  
1. Constructor injection using the @VALUE("User1") annotation.  
2. UserBox is a pure Java configuration class of jBeanBox. This Java class is a pure Java class (unlike the Java configuration class in Spring is a very special class, it will generate a proxy class at runtime), which can be used. Common design patterns such as class inheritance and method rewriting. In this example its create method manually generates a User("User2") object.  
3. The third is to dynamically generate a BeanBox configuration, dynamically configure its constructor injection, and inject the value "User3".  
4. The fourth is also a dynamic configuration that demonstrates the field injection, with the injected value being the constant "User4".  
5. The fifth is a demonstration of method injection. The injection parameters are: method name, parameter type, and actual parameters.  
6. The sixth is setPostConstruct injection, equivalent to the @PostConstruct annotation, that is, the method executed immediately after the bean is generated is the init() method.  
7. The seventh is more complicated. ctx is a new context instance. It first gets the fixed configuration of User.class, then adds an AOP aspect to its setName method, and then injects the "name" field into autowired type.   String type, but before this String class is bound to the string "7", the string "7" is bound to H2.class, H7 inherits from UserBox, UserBox returns "User2", but Since H7 itself is configured as a value type "User7", the final output is "User7".  
8. The eighth is relatively simple, because the setName method has been added an AOP interceptor and the parameter has been changed to "User8".  
9. The ninth is because the ctx context ends, all singletons are executed by @PreDestroy, which is a standard JSR330 annotation.  

Above example mainly demonstrates the Java method configuration of jBeanBox. The Java method can be executed dynamically, or it can be executed as a fixed configuration in the defined BeanBox class. The fixed configuration can lay down the configuration keynote. When you need to change, you can use the same Java method to adjust (because it is the same BeanBox object) or even temporarily create a new configuration, so jBeanBox has the advantages of fixed configuration and dynamic configuration. In addition, when there is no source code, for example, to configure an instance of a third-party library, all annotation methods are not used at this time, and the only Java configuration method that can be used is the only one.  

The value() method in above example is a global method statically introduced from the JBEANBOX class. The source code for this example is located in HelloWorld.java under the unit test directory.

### jBeanBox Annotation Configuration
jBeanBox not only supports Java mode configuration, but also supports annotation mode configuration. It supports the following annotations:  
@INJECT is similar to the @Inject annotation in JSR, but allows the addition of the target class as a parameter  
@POSTCONSTRUCT is equivalent to the @PostConstruct annotation in JSR  
@PREDESTROY is equivalent to @PreDestroy annotation in JSR  
@VALUE is similar to the @Value annotation in Spring  
@PROTOTYPE is equivalent to @Prototype annotation in Spring  
@AOP is used to customize AOP annotations. See the AOP section for details.  

jBeanBox can also support below JSR or Spring Annotations：  
JSR Annotations：@PostConstruct, @PreDestroy, @Inject, @Singleton, @scope(“prototype”), @scope(“singleton”)  
Spring Annotations：@Autowired @Prototype  

Because everyone is familiar with annotation method configuration, here has no detailed introduction, in jBeanBox\test directory can find an "AnnotationInjectTest.java" file, demonstrating the use of various annotation mode configuration. To disable JSR, Spring annotations, can use ctx.setAllowSpringJsrAnnotation(false) method. To disable all annotations (that means only Java configurationcan be used) use ctx.setAllowAnnotation(false) method.  

Regarding the annotation mode configuration, jBeanBox is different from other IOC tools in that it does not support the three JSR annotations: @Qualifer, @Name, and @Provider. This is because the author thinks that these 
three annotations can be implemented with existing annotations in jBeanBox. Such as:
```
@Inject @Named("JDBC-URL") private String url;
In jBeanBox can be replaced by:
@INJECT(JDBC_URL.class) private String url; //where JDBC_URL.class is a BeanBox class
or
@VALUE("$JDBC-URL") private String url; //$JDBC-URL value can be translated by configuring the ValueTranslator in the BeanBoxContext.

Another example:
@Named("p") public class Person {}
In jBeanBox, the Person class already has a unique ID: Person.class, no need to define an extra "P" as the ID, all statically defined classes, its class itself is a unique ID. jBeanBox is a singleton class for statically defined classes, so each time ctx.getBean(Person.class) gets the same singleton object.
```
The problem of @Named is that it is a string type, cannot be quickly located to the configuration file using IDE. When the project is configured lot injections, it is hard to maintenance.
jBeanBox is an IOC tool that does not need to define a Bean ID. Note: If it is a manually created BeanBox configuration, the default is non-singleton class. If you use the setSingleton(true) method to hard change to a singleton, then the question is, what is its ID? Very simple, its unique ID is the dynamically created configuration instance itself. BeanBox box1=new BeanBox(A.class).setSingeton(true), then each time ctx.getBeanBox(box1) gets the same A type of singleton object. Of course, you can also use ctx.bind("id1",box1), which is equivalent to manually binding an ID value "id1", which can be obtained with getBean("id1"). jBeanBox doesn't have the ability to auto-scan, pre-create singletons, so it's very fast to start. If someone has automatic scanning, pre-created singletons, pre-bound ID names, etc., you must manually write a tool class to achieve this purpose (jBeanBox is not available), such as calling ctx.getBean at the beginning of the program run ( A.class) will temporarily store a singleton class of A in the context, which will be taken directly from the cache on the next visit.

### jBeanBox's Java configuration methods
The example one is a general demonstration of the Java mode configuration of jBeanBox, and new let's go back to explain all Java configuration mothods in detail:
* setAsValue(Object) configures the current BeanBox as a constant value, equivalent to setTarget(Obj)+setPureVale(true)
* setPrototype(boolean) If the argument is true, it means that it is a non-singleton, contrary to the setSingleton method.
* injectConstruct(Class<?>, Object...) Sets the constructor injection. The parameters are class, constructor parameter type, and parameters.
* injectMethod(String, Object...) Sets a method injection. The parameters are method name, parameter type, and parameters.
* addAopToMethod(Object, Method) Add AOP to a method, the parameters are AOP class or instance, method
* addMethodAop(Object, String, Class<?>...) Add AOP to a method, the parameters are AOP class or instance, method name, parameter type
* addBeanAop(Object, String) Adds AOP to the entire bean. The parameters are AOP class or instance, and method rules (such as "setUser*").
* setPostConstruct(String) sets a PostConstruct method name with the same effect as @PostConstruct annotation
* setPreDestroy(String) sets a PreDestroy method name with the same effect as @PreDestroy annotation
* injectField(String, BeanBox) Injects a field, the parameter is the field name, BeanBox instance, and its equivalent annotation is @INJECT
* setProperty(String, Object) is equivalent to the injectValue method
* injectValue(String, Object) Injects a field, the parameter is the field name, the object instance, and the annotation that can be compared with it is @VALUE
* setTarget(Object) is destined for the current bean's target. In addition, when bind("7", User.class), setTarget("7") is equivalent to setTarget(User.class).
* setPureValue(boolean) indicates that the target is no longer the target, but returns as a pure value. The "7" on the upstream will return the string "7".
* setBeanClass(Class<?>) sets the final target class of the current BeanBox. All configurations are based on this class.
* setSingleton(Boolean) is the opposite of setPrototype
* setConstructor(Constructor<?>) sets a constructor
* setConstructorParams(BeanBox[]) sets the parameters of the constructor, which is used in conjunction with the upstream
* setPostConstruct(Method) sets a PostConstruct method with the same effect as @PostConstruct annotation
* setPreDestroy(Method) sets a PreDestroy method name with the same effect as @PreDestroy annotation

The Java configuration, there are 2 special methods in BeanBox class: create和config. See below:
```
public static class DemoBox extends BeanBox {

		public Object create(Caller caller) {
			A a = new A();
			a.field1 = caller.getBean(B.class);
			return a;
		}

		public void config(Object o, Caller caller) {
			((A) o).field2 = caller.getBean(C.class);
		}
	}
```
The above example shows that the bean created in DemoBox is generated by the create method and modified by the config method. The Caller parameter in the create and config methods can be omitted if you don't need to use this Caller parameter to load other beans.


### AOP for jBeanBox (for faceted programming)
Most of the jBeanBox functions can be implemented in either Java configuration or annotation configuration. Similarly, there are two ways to support AOP:

#### Java mode AOP configuration
* someBeanBox.addMethodAop(Object, String, Class<?>...) Add AOP to a method, the parameters are AOP class or instance, method name, parameter type
* someBeanBox.addBeanAop(Object, String) Adds AOP to the entire bean. The parameters are AOP class or instance, and method rules (such as "setUser*").
* someBeanBoxContext.addGlobalAop(Object, Object, String); Adds an AOP rule to the entire context. The parameters are AOP class or instance, class or class name rule, and method name rule.
The above three methods correspond to three different levels of AOP rules, the first method is only for the method, the second method is for the entire class, and the third method is for the entire context. The following is an example of a Java configuration for AOP:
```
public static class AopDemo1 {
		String name;
		String address;
		String email;
        //getter & setters...
	}

	public static class MethodAOP implements MethodInterceptor { 
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "1";
			return invocation.proceed();
		}
	}

	public static class BeanAOP implements MethodInterceptor { 
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "2";
			return invocation.proceed();
		}
	}

	public static class GlobalAOP implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "3";
			return invocation.proceed();
		}
	}

	public static class AopDemo1Box extends BeanBox {
		{
			this.injectConstruct(AopDemo1.class, String.class, value("0"));
			this.addMethodAop(MethodAOP.class, "setName", String.class);
			this.addBeanAop(BeanAOP.class, "setAddr*");
		}
	}

	@Test
	public void aopTest1() {
		JBEANBOX.bctx().bind("3", GlobalAOP.class);
		JBEANBOX.bctx().addGlobalAop("3", AopDemo1.class, "setEm*");
		AopDemo1 demo = JBEANBOX.getBean(AopDemo1Box.class);
		demo.setName("--");
		Assert.assertEquals("1", demo.name);
		demo.setAddress("--");
		Assert.assertEquals("2", demo.address);
		demo.setEmail("--");
		Assert.assertEquals("3", demo.email);
	}
```
The above naming rules use "*" as a fuzzy matching character, representing any length, any character.

#### Annotation mode AOP configuration
The annotation method AOP has only two types, for method and for class.
The annotation method requires a special annotation @AOP, which is used to customize the AOP annotations. The usage examples are as follows:
```
public static class Interceptor1 implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "1";
			return invocation.proceed();
		}
	}

	public static class Interceptor2 implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			invocation.getArguments()[0] = "2";
			return invocation.proceed();
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	@AOP
	public static @interface MyAop1 {
		public Class<?> value() default Interceptor1.class;

		public String method() default "setNa*";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	@AOP
	public static @interface MyAop2 {
		public Class<?> value() default Interceptor2.class;
	}

	@MyAop1
	public static class AopDemo1 {
		String name;
		String address;

		public void setName(String name) {
			this.name = name;
		}

		@MyAop2
		public void setAddress(String address) {
			this.address = address;
		}
	}

	@Test
	public void aopTest1() {
		AopDemo1 demo = JBEANBOX.getBean(AopDemo1.class);
		demo.setName("--");
		Assert.assertEquals("1", demo.name);
		demo.setAddress("--");
		Assert.assertEquals("2", demo.address);
	}
```
The AOP mentioned in this article is for the interface of the Aop alliance federation standard. It has been included in the jBeanBox and does not need to be introduced separately (of course, there is no problem with repeated introduction). The Aop alliance federation standard is a useful interface to achieve interchangeability between various AOP implementations. Based on it, jBeanBox can replace Spring's kernel and use its declarative transaction. This interchangeability can be achieved. The premise is that Spring's declarative transaction implementation (such as TransactionInterceptor) also implements the Aop alliance federation standard interface MethodInterceptor.  

Since the 2.4.8 version, the ABean function has been cut off, and the unused pre-, post-, and abnormal aspect functions have been removed. Only the functions of the AOP alliance standard interface MethodInterceptor have been retained (note that there is also an interface with the same name in CGLIB). , don't confuse). The class that implements the MethodInterceptor interface, usually called Interceptor, but saves it in the jBeanBox, also called it AOP, after all, writing addBeanAop is simpler than writing addBeanInterceptor.  


### About circular dependencies
jBeanBox supports loop dependency detection. If a loop dependency injection is found (such as injecting B in the A constructor and injecting A in the constructor of B), a BeanBoxException runtime exception will be thrown.  
However, circular dependency injections that occur in such fields or methods are allowed in jBeanBox:  
```
public static class A {
		@Inject
		public B b;
	}

public static class B {
		@Inject
		public A a;
	}

A a = JBEANBOX.getBean(A.class);
Assert.assertTrue(a == a.b.a);//true
```
### jBeanBox supports multiple contexts and Bean lifecycle
jBeanBox supports multiple context instances (BeanBoxContext), and each context instance does not. For example, a User.class can generate three singletons in different contexts (annotations, Java) in three contexts. These three "singletons" are unique relative to the current context, and their properties and their respective Configuration related.  
  
The JBEANBOX.getBean() method takes advantage of a default global context, which can be retrieved using the JBEANBOX.bctx() method, so if you don't need multiple contexts in a project, you can use the JBEANBOX.getBean() method directly. Get the instance, which saves a line of code that creates a new context.  

Each instance of BeanBoxContext maintains configuration information, singleton cache, etc. internally. After the close method of the BeanBoxContext instance is called, its configuration information and singleton are cleared. Of course, before the emptying, all singletons of PreDestroy are cleared. The method (if any) is called to run. So for the context that needs to call back the PreDestroy method, don't forget to call the close method when closing. For the default global context, this is the JBEANBOX.close() method.  

Detailed methods of BeanBoxContext are explained in detail:  
* reset() This static method resets all static global configurations and calls the close method of the default context instance.
* close() first calls the PreDestroy method (if any) of the singleton instance in the current context cache, then clears the current context's cache.
* getBean(Object) returns a bean based on the target object (can be any object type), throws an exception if it is not found
* getInstance(Class<T>) returns an instance of type T based on the target class T, throwing an exception if not found
* getBean(Object, boolean) returns a bean according to the target object. If the second parameter is false, it returns Empty.class if it is not found.
* getInstance(Class<T>, boolean) returns an instance of type T according to the target class T. If the second parameter is false, it returns Empty.class if it is not found.
* bind(Object, Object) Binds an ID to the target class, for example: ctx.bind("A","B").bind("B".C.class), then you can use getBean("A" later. ) Get an instance of C
* addGlobalAop(Object, String, String) Add an AOP in the current context (see the AOP section for details). The second parameter is the class name fuzzy matching rule, such as "com.tom.*" or "*.tom". , * is only allowed to appear at the beginning and end (can appear at the same time, or one can not appear), the third parameter is the method name fuzzy matching rules, such as "setUser*" or "*user".
* addGlobalAop(Object, Class<?>, String) Adds an AOP in the current context. The second parameter is the specified class (which will match all classes that start with the specified class name, for example, the specified class is abCclass, then abCXX .class will also be matched), and the third parameter is the method name fuzzy matching rule.
* getBeanBox(Class<?>) Gets a BeanBox instance of a class, such as a annotation annotation class. You can use this method to get a BeanBox instance, and then add and modify its configuration. This is the combination of fixed configuration and dynamic configuration.
* setAllowAnnotation(boolean) Sets whether to allow annotations in the class to be read. If set to flase, the jBeanBox only allows pure Java configuration. The default is true.
* setAllowSpringJsrAnnotation(boolean) Sets whether to allow partial annotation of JSR330/JSR350 and Spring in the class to be read for compatibility. The default is true.
* setValueTranslator(ValueTranslator) sets how to parse the content in the @VALUE annotation, such as @VALUE("#user"). The system returns the "#user" string by default. If you need different parsing, such as reading the property text. In the value, you need to set an instance that implements the ValueTranslator interface.

### Performance of jBeanBox
The following is the comparison of the performance of jBeanBox with other IOC tools (only compare the DI injection function, build an instance tree composed of 6 objects), it can be seen that jBeanBox creates a non-singleton bean half speed of Guice and 45 times faster than Spring. The test project is located in: [di-benchmark] (https://github.com/drinkjava2/di-benchmark)
```
Runtime benchmark, fetch new bean for 500000 times:
---------------------------------------------------------
                     Vanilla|    31ms
                       Guice|  1154ms
                     Feather|   624ms
                      Dagger|   312ms
                       Genie|   609ms
                        Pico|  4555ms
              jBeanBoxNormal|  2075ms
            jBeanBoxTypeSafe|  2371ms
          jBeanBoxAnnotation|  2059ms
     SpringJavaConfiguration| 92149ms
     SpringAnnotationScanned| 95504ms
     
     
Split Starting up DI containers & instantiating a dependency graph 4999 times:
-------------------------------------------------------------------------------
                     Vanilla| start:     0ms   fetch:     0ms
                       Guice| start:  1046ms   fetch:  1560ms
                     Feather| start:     0ms   fetch:   109ms
                      Dagger| start:    46ms   fetch:   173ms
                        Pico| start:   376ms   fetch:   217ms
                       Genie| start:   766ms   fetch:   247ms
              jBeanBoxNormal| start:    79ms   fetch:   982ms
            jBeanBoxTypeSafe| start:     0ms   fetch:   998ms
          jBeanBoxAnnotation| start:     0ms   fetch:   468ms
     SpringJavaConfiguration| start: 51831ms   fetch:  1834ms
     SpringAnnotationScanned| start: 70712ms   fetch:  4155ms

Runtime benchmark, fetch singleton bean for 5000000 times:
---------------------------------------------------------
                     Vanilla|    47ms
                       Guice|  1950ms
                     Feather|   624ms
                      Dagger|  2746ms
                       Genie|   327ms
                        Pico|  3385ms
              jBeanBoxNormal|   188ms
            jBeanBoxTypeSafe|   187ms
          jBeanBoxAnnotation|   171ms
     SpringJavaConfiguration|  1061ms
     SpringAnnotationScanned|  1045ms
```
Although most of the IOC tools are used in singleton cases, the performance is almost the same (because it is taken from the cache), but if you encounter a situation where you must generate a non-single instance, such as generating a new page instance each time, Spring is not fast enough. And for the starting speed, it is pretty slow.


The above is the introduction of jBeanBox, there is no other documents, because after all, jBeanBox's core source code is only ~1500 lines (third-party tools such as CGLIB, JSR interface etc. are not counted). If you have any questions of jBeanBox, to check its source code is a easy solution.

More demos of jBeanBox can also be seen in the jSqlBox project (data source configuration, declarative transaction examples, etc.).